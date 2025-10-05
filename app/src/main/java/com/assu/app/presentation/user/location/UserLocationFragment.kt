package com.assu.app.presentation.user.location

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.assu.app.data.dto.location.ViewportQuery
import com.assu.app.databinding.FragmentUserLoactionBinding
import com.assu.app.domain.model.location.StoreOnMap
import com.assu.app.presentation.base.BaseFragment
import com.assu.app.presentation.user.review.store.UserReviewStoreActivity
import com.assu.app.ui.location.UserLocationViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.kakao.vectormap.*
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.assu.app.R

@AndroidEntryPoint
class UserLocationFragment :
    BaseFragment<FragmentUserLoactionBinding>(R.layout.fragment_user_loaction) {

    // Kakao Map
    private lateinit var mapView: MapView
    private var kakaoMap: KakaoMap? = null
    private var mapReady = false

    // 마커 스타일 2종
    private var partnerStyles: LabelStyles? = null
    private var normalStyles: LabelStyles? = null

    // 처음 한 번만 말풍선 보여줌 플래그
    private var shownPartnerBubbleOnce = false

    // 현재 위치 라벨
    private var myLocStyles: LabelStyles? = null
    private var myLocLabel: Label? = null

    //private val SEOUL_CITY_HALL = LatLng.from(37.4947, 126.9576)

    // 마지막으로 성공한 현재 위치(재진입 / 되돌아가기용 캐시)
    private var lastMyLatLng: LatLng? = null

    private var poiLayer: LabelLayer? = null
    private var storeStyles: LabelStyles? = null
    private val labelToStore = mutableMapOf<Label, StoreOnMap>()

    private val vm: UserLocationViewModel by viewModels()

    private val fused by lazy { LocationServices.getFusedLocationProviderClient(requireContext()) }
    private val permLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = (result[ACCESS_FINE_LOCATION] == true) || (result[ACCESS_COARSE_LOCATION] == true)
        if (granted) goToMyLocation()
    }

    // 기본 위치(서울 시청)
    private val DEFAULT_ZOOM = 17

    override fun initView() {
        binding.viewLocationSearchBar.setOnClickListener { navigateToSearch() }
        binding.ivLocationSearchIc.setOnClickListener { navigateToSearch() }
        binding.tvLocationHint.setOnClickListener { navigateToSearch() }
        binding.ivUserGoBack.setOnClickListener { goToMyLocation() }

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

                    // 마커 스타일 생성 (24dp 권장)
                    val blue = vectorToBitmap(R.drawable.ic_user_location_blue, 39)
                    val gray = vectorToBitmap(R.drawable.ic_user_location_gray, 24)

                    partnerStyles = map.labelManager?.addLabelStyles(
                        LabelStyles.from(LabelStyle.from(blue).setAnchorPoint(0.5f, 1.0f))
                    )
                    normalStyles = map.labelManager?.addLabelStyles(
                        LabelStyles.from(LabelStyle.from(gray).setAnchorPoint(0.5f, 1.0f))
                    )

                    val locBmp = vectorToBitmap(R.drawable.ic_present_location, 24) // 원하는 아이콘
                    myLocStyles = map.labelManager?.addLabelStyles(
                        LabelStyles.from(
                            LabelStyle.from(locBmp).setAnchorPoint(0.5f, 1.0f)
                        )
                    )

                    poiLayer = map.labelManager?.layer

                    // 마커 클릭 → 캡슐 표시 + 처음 한 번 말풍선 표시
                    map.setOnLabelClickListener(object : KakaoMap.OnLabelClickListener {
                        override fun onLabelClicked(map: KakaoMap, layer: LabelLayer, label: Label): Boolean {
                            val item = labelToStore[label] ?: return true
                            showCapsule(item)

                            // 버블은 "컨텐츠 있고 아직 한 번도 안 보여줬을 때만" 1회 노출
                            if (!shownPartnerBubbleOnce && isPartnerVisual(item)) {
                                shownPartnerBubbleOnce = true
                                showSpeechBubbleOver(item.latitude, item.longitude, item.name ?: "")
                            }
                            else hideBubble()

                            return true
                        }
                    })
1
                    kakaoMap?.setOnMapClickListener { _, _, _, _ ->
                        hideCapsuleAndBubble()
                    }

                    // 카메라 이동 종료 시 재조회
                    map.setOnCameraMoveEndListener { _, _, _ -> requestNearbyFromCurrentViewport() }
                    goToMyLocation()
                    //moveCameraAndQuery(SEOUL_CITY_HALL.latitude, SEOUL_CITY_HALL.longitude)
                    requestLocationPermissionsIfNeeded()
                }
            }
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.state.collect { state ->
                    when (state) {
                        is UserLocationViewModel.UiState.Success -> drawMarkers(state.items)
                        is UserLocationViewModel.UiState.Fail ->
                            Log.e("UIState", "Fail: ${state.code}, ${state.message}")
                        is UserLocationViewModel.UiState.Error ->
                            Log.e("UIState", "Error", state.t)
                        else -> Unit
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
        }
    }

    private fun moveCameraAndQuery(lat: Double, lng: Double) {
        if (!mapReady || kakaoMap == null) return
        kakaoMap?.moveCamera(
            CameraUpdateFactory.newCenterPosition(LatLng.from(lat, lng), DEFAULT_ZOOM)
        )
        //requestNearbyFromCurrentViewport()
    }

    private fun centerToMyLocation(lat: Double, lng: Double) {
        lastMyLatLng = LatLng.from(lat, lng)
        showCurrentLocation(lat, lng)
        moveCameraAndQuery(lat, lng)
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

    // 마커 그림
    private fun drawMarkers(items: List<StoreOnMap>) {
        if (!mapReady) return
        val layer = poiLayer ?: kakaoMap?.labelManager?.layer ?: return
        val pStyles = partnerStyles ?: return
        val nStyles = normalStyles ?: return

        labelToStore.clear()
        layer.removeAll()

        items.forEach { s ->
            val styles = if (isPartnerVisual(s)) pStyles else nStyles
            val label = layer.addLabel(
                LabelOptions.from(LatLng.from(s.latitude, s.longitude))
                    .setStyles(styles)
                    .setTag(s.storeId.toString())
            )
            labelToStore[label] = s
        }
    }

    // 하단 캡슐(아이템 프래그먼트)에 바인딩
    private fun showCapsule(item: StoreOnMap) {
        val frag = childFragmentManager.findFragmentById(R.id.fv_user_location_item) as? UserLocationItemFragment
            ?: UserLocationItemFragment().also {
                childFragmentManager.beginTransaction()
                    .replace(R.id.fv_user_location_item, it)
                    .commitNowAllowingStateLoss()
            }

        // criterionType/optionType → 설명 문자열로 포매팅
        val description = formatCriterion(item)
        frag.bind(
            UserLocationItemFragment.UserStoreItem(
                shopName = item.name ?: "-",
                criterionType = description,
                rating = (item.rate ?: 0).toFloat()
            )
        )

        binding.includeSpeechBubble.visibility = View.VISIBLE
        binding.fvUserLocationItem.visibility = View.VISIBLE

        // 카드 클릭 시 상세로 이동(기존 동작 유지)
        binding.fvUserLocationItem.setOnClickListener {
            startActivity(Intent(requireContext(), UserReviewStoreActivity::class.java)
                .putExtra("storeId", item.storeId)
                .putExtra("storeName", item.name))
        }
    }

    // 서버 응답을 사람이 읽기 쉬운 문구로
    private fun formatCriterion(s: StoreOnMap): String {
        return when (s.optionType) {
            "SERVICE" -> when (s.criterionType) {
                "PRICE" -> {
                    val cost = s.cost?.toString() ?: "-"
                    val gift = s.category ?: "상품"
                    "${cost}원 이상 구매 시 ${gift} 증정"
                }
                "HEADCOUNT" -> {
                    val people = s.people?.toString() ?: "-"
                    val gift = s.category ?: "상품"
                    "${people}명 이상 방문 시 ${gift} 증정"
                }
                else -> "서비스 혜택"
            }

            "DISCOUNT" -> when (s.criterionType) {
                "PRICE" -> {
                    val cost = s.cost?.toString() ?: "-"
                    val rate = s.discountRate?.toString() ?: "-"
                    "${cost}원 이상 구매 시 ${rate}% 할인"
                }
                "HEADCOUNT" -> {
                    val people = s.people?.toString() ?: "-"
                    val rate = s.discountRate?.toString() ?: "-"
                    "${people}명 이상 방문 시 ${rate}% 할인"
                }
                else -> "할인 혜택"
            }

            else -> s.address.toString()
        }
    }

    private fun hideBubble() {
        binding.includeSpeechBubble.visibility = View.GONE
    }
    private fun hideCapsule() {
        binding.fvUserLocationItem.visibility = View.GONE
    }
    private fun hideCapsuleAndBubble() {
        hideCapsule()
        hideBubble()
    }

    private fun showSpeechBubbleOver(lat: Double, lng: Double, title: String) {
        if (!mapReady || kakaoMap == null) return

        val bubbleBinding = binding.includeSpeechBubble
        val bubble = bubbleBinding

        // (선택) 말풍선 내부 텍스트가 있으면 채우기
        // bubbleBinding.tvTitle.text = title

        // 지도 좌표 → MapView 내부 스크린 좌표
        val screenPt = kakaoMap!!.toScreenPoint(LatLng.from(lat, lng)) ?: return

        // bubble이 배치될 "부모 뷰" 좌표계와 MapView 좌표계의 차이를 보정
        val parent = bubble.parent as ViewGroup
        val parentLoc = IntArray(2)
        val mapLoc = IntArray(2)
        parent.getLocationOnScreen(parentLoc)
        binding.userLocationMapView.getLocationOnScreen(mapLoc)

        val offsetX = mapLoc[0] - parentLoc[0]
        val offsetY = mapLoc[1] - parentLoc[1]

        // 측정 이후 배치 (width/height 확보용)
        bubble.post {
            val anchorYOffset = bubble.height + dp(8) // 마커 위 8dp
            bubble.x = offsetX + screenPt.x - bubble.width / 2f
            bubble.y = offsetY + screenPt.y - anchorYOffset
            bubble.visibility = View.VISIBLE
            bubble.bringToFront()
        }
    }

    // dp → px
    private fun dp(value: Int): Float =
        value * resources.displayMetrics.density

    private fun isPartnerVisual(s: StoreOnMap): Boolean {
        // 제휴 내용이 있는지 확인
        val hasContent = listOf(
            s.criterionType,
            s.optionType,
            s.category
        ).any { it != null } || listOf(
            s.cost,
            s.people,
            s.discountRate
        ).any { it != null }

        return hasContent
    }

    @SuppressLint("MissingPermission")
    private fun goToMyLocation() {
        val fineGranted   = ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(requireContext(), ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            permLauncher.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
            return
        }

        // UX 빠르게: 마지막 좌표로 먼저 이동
        lastMyLatLng?.let { moveCameraAndQuery(it.latitude, it.longitude) }

        fused.lastLocation
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    centerToMyLocation(loc.latitude, loc.longitude)
                } else {
                    val cts = CancellationTokenSource()
                    fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                        .addOnSuccessListener { cur ->
                            if (cur != null) centerToMyLocation(cur.latitude, cur.longitude)
                        }
                }
            }
    }
}