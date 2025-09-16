package com.example.assu_fe_app.presentation.user.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.location.ViewportQuery
import com.example.assu_fe_app.databinding.FragmentUserLoactionBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.user.review.store.UserReviewStoreActivity
import com.example.assu_fe_app.ui.location.UserLocationViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.kakao.vectormap.*
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserLocationFragment :
    BaseFragment<FragmentUserLoactionBinding>(R.layout.fragment_user_loaction) {

    // Kakao Map
    private lateinit var mapView: MapView
    private var kakaoMap: KakaoMap? = null
    private var mapReady = false

    // 현재 위치 라벨 (초기엔 사용 안 함)
    private var myLocStyles: LabelStyles? = null
    private var myLocLabel: Label? = null

    // ViewModel
    private val vm: UserLocationViewModel by viewModels()

    // Location (나중에 사용할 예정)
    private val fused by lazy { LocationServices.getFusedLocationProviderClient(requireContext()) }
    private val permLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        // ✅ 권한 결과와 무관하게 지금은 아무 것도 하지 않음 (시청 기준 유지)
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        Log.d("Permission", "location granted=$granted (but keeping City Hall view)")
    }

    // 기본 위치(서울 시청)
    private val DEFAULT_LATITUDE = 37.5662952
    private val DEFAULT_LONGITUDE = 126.9779451
    private val DEFAULT_ZOOM = 15

    override fun initView() {
        binding.viewLocationSearchBar.setOnClickListener { navigateToSearch() }
        binding.ivLocationSearchIc.setOnClickListener { navigateToSearch() }
        binding.tvLocationHint.setOnClickListener { navigateToSearch() }

        binding.userLocationMapView.setOnClickListener {
            binding.includeSpeechBubble.visibility = View.VISIBLE
            binding.fvUserLocationItem.visibility = View.VISIBLE
        }

        binding.fvUserLocationItem.setOnClickListener {
            startActivity(Intent(requireContext(), UserReviewStoreActivity::class.java))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = binding.userLocationMapView

        mapView.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() { Log.d("KakaoMap", "onMapDestroy (UserLocationFragment)") }
                override fun onMapError(error: Exception) { Log.e("KakaoMap", "onMapError (UserLocationFragment)", error) }
            },
            object : KakaoMapReadyCallback() {
                override fun onMapReady(map: KakaoMap) {
                    kakaoMap = map
                    mapReady = true

                    // 현재 위치 아이콘 스타일(나중에 사용할 예정)
                    val locBmp = vectorToBitmap(R.drawable.ic_present_location, 24)
                    val myLocStyle = LabelStyle.from(locBmp).setAnchorPoint(0.5f, 1.0f)
                    myLocStyles = map.labelManager?.addLabelStyles(LabelStyles.from(myLocStyle))

                    // 카메라 이동 종료 시 현재 뷰포인트 재조회
                    map.setOnCameraMoveEndListener { _, _, _ -> requestNearbyFromCurrentViewport() }

                    // 항상 서울시청 기준으로 먼저 이동/조회
                    moveToDefaultThenQuery()

                    // 권한 요청은 하되, 지금은 현재 위치로 이동/표시는 안 함
                    requestLocationPermissionsIfNeeded()
                }
            }
        )

        // ViewModel state collect
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.state.collect { state ->
                    when (state) {
                        is UserLocationViewModel.UiState.Idle -> Unit
                        is UserLocationViewModel.UiState.Loading -> Log.d("UIState", "Loading…")
                        is UserLocationViewModel.UiState.Success -> Log.d("UIState", "Loaded ${state.items.size} stores")
                        is UserLocationViewModel.UiState.Fail -> Log.e("UIState", "Fail: ${state.code}, ${state.message}")
                        is UserLocationViewModel.UiState.Error -> Log.e("UIState", "Error", state.t)
                    }
                }
            }
        }
    }

    override fun initObserver() = Unit

    private fun navigateToSearch() {
        startActivity(Intent(requireContext(), UserLocationSearchActivity::class.java))
    }

    // ===== MapView lifecycle =====
    override fun onResume() { super.onResume(); if (::mapView.isInitialized) mapView.resume() }
    override fun onPause() { super.onPause(); if (::mapView.isInitialized) mapView.pause() }
    override fun onDestroyView() { super.onDestroyView(); if (::mapView.isInitialized) mapView.removeAllViews() }

    // ===== Permission (요청만; 현재 위치 이동/표시는 하지 않음) =====
    private fun requestLocationPermissionsIfNeeded() {
        val fine = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!fine && !coarse) {
            permLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        } else {
            Log.d("Permission", "already granted (keeping City Hall view)")
            // 나중에 현재 위치로 바꿀 때 fetchLocationAndQuery() 호출 예정
        }
    }

    // ===== 현재 위치로 바꾸는 함수(지금은 호출 안 함) =====
    @SuppressLint("MissingPermission")
    private fun fetchLocationAndQuery() {
        fused.lastLocation
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    moveCameraAndQuery(loc.latitude, loc.longitude)
                    showCurrentLocation(loc.latitude, loc.longitude)
                } else {
                    val cts = CancellationTokenSource()
                    fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                        .addOnSuccessListener { cur ->
                            if (cur != null) {
                                moveCameraAndQuery(cur.latitude, cur.longitude)
                                showCurrentLocation(cur.latitude, cur.longitude)
                            }
                        }
                        .addOnFailureListener {
                            Log.e("Location", "getCurrentLocation failed", it)
                        }
                }
            }
            .addOnFailureListener {
                Log.e("Location", "lastLocation failed", it)
            }
    }

    private fun moveCameraAndQuery(lat: Double, lng: Double) {
        if (!mapReady || kakaoMap == null) return
        kakaoMap?.moveCamera(
            CameraUpdateFactory.newCenterPosition(LatLng.from(lat, lng), DEFAULT_ZOOM)
        )
        requestNearbyFromCurrentViewport()
    }

    private fun moveToDefaultThenQuery() {
        // ✅ 기본: 서울시청으로만 이동/조회 (현재 위치 마커는 표시하지 않음)
        moveCameraAndQuery(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
    }

    private fun requestNearbyFromCurrentViewport() {
        if (!mapReady || kakaoMap == null || mapView.width == 0 || mapView.height == 0) return
        val vp = mapView.getViewportCorners(kakaoMap!!)
        val query = ViewportQuery(
            lng1 = vp.nw.longitude, lat1 = vp.nw.latitude,
            lng2 = vp.ne.longitude, lat2 = vp.ne.latitude,
            lng3 = vp.se.longitude, lat3 = vp.se.latitude,
            lng4 = vp.sw.longitude, lat4 = vp.sw.latitude
        )
        Log.d("Viewport", "query=$query")
        vm.load(query)
    }

    // ===== 현재 위치 라벨 표시(지금은 호출 안 함) =====
    private fun showCurrentLocation(lat: Double, lng: Double) {
        if (!mapReady || kakaoMap == null) return
        val styles = myLocStyles ?: return
        val layer = kakaoMap!!.labelManager?.layer ?: return

        myLocLabel?.remove()
        val opts = LabelOptions.from(LatLng.from(lat, lng)).setStyles(styles)
        myLocLabel = layer.addLabel(opts)
    }

    // ===== Viewport 계산 =====
    fun MapView.getViewportCorners(kakaoMap: KakaoMap): Viewport {
        val w = width
        val h = height
        val nw = kakaoMap.fromScreenPoint(0, 0)!!
        val ne = kakaoMap.fromScreenPoint(w, 0)!!
        val se = kakaoMap.fromScreenPoint(w, h)!!
        val sw = kakaoMap.fromScreenPoint(0, h)!!

        val lngs = listOf(nw.longitude, ne.longitude, se.longitude, sw.longitude)
        val lats = listOf(nw.latitude, ne.latitude, se.latitude, sw.latitude)

        val minLng = lngs.minOrNull() ?: 0.0
        val maxLng = lngs.maxOrNull() ?: 0.0
        val minLat = lats.minOrNull() ?: 0.0
        val maxLat = lats.maxOrNull() ?: 0.0

        return Viewport(minLng, minLat, maxLng, maxLat, nw, ne, se, sw)
    }

    data class Viewport(
        val minLng: Double, val minLat: Double,
        val maxLng: Double, val maxLat: Double,
        val nw: LatLng, val ne: LatLng, val se: LatLng, val sw: LatLng
    )

    // ===== 벡터 → 비트맵 변환 =====
    private fun vectorToBitmap(resId: Int, targetDp: Int): Bitmap {
        val d = ContextCompat.getDrawable(requireContext(), resId)
            ?: throw IllegalArgumentException("Drawable not found")
        val density = resources.displayMetrics.density
        val w = (targetDp * density).toInt().coerceAtLeast(1)
        val h = (targetDp * density).toInt().coerceAtLeast(1)
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        d.setBounds(0, 0, canvas.width, canvas.height)
        d.draw(canvas)
        return bmp
    }
}