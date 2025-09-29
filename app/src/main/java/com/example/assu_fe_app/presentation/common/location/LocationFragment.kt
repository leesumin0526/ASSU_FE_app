package com.example.assu_fe_app.presentation.common.location

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.UserRole
import com.example.assu_fe_app.data.dto.location.LocationAdminPartnerSearchResultItem
import com.example.assu_fe_app.data.dto.location.ViewportQuery
import com.example.assu_fe_app.data.dto.partnership.OpenContractArgs
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import com.example.assu_fe_app.databinding.FragmentLoactionBinding
import com.example.assu_fe_app.domain.model.location.AdminOnMap
import com.example.assu_fe_app.domain.model.location.PartnerOnMap
import com.example.assu_fe_app.domain.model.partnership.ProposalPartnerDetailsModel
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.contract.PartnershipContractDialogFragment
import com.example.assu_fe_app.presentation.common.contract.toContractData
import com.example.assu_fe_app.presentation.common.location.adapter.AdminPartnerLocationAdapter
import com.example.assu_fe_app.presentation.common.location.adapter.LocationSharedViewModel
import com.example.assu_fe_app.ui.chatting.ChattingViewModel
import com.example.assu_fe_app.ui.location.AdminPartnerLocationViewModel
import com.example.assu_fe_app.ui.map.MapBridgeViewModel
import com.example.assu_fe_app.ui.map.MapEvent
import com.example.assu_fe_app.ui.partnership.PartnershipViewModel
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
import javax.inject.Inject

@AndroidEntryPoint
class LocationFragment :
    BaseFragment<FragmentLoactionBinding>(R.layout.fragment_loaction) {
    private val sharedViewModel: LocationSharedViewModel by activityViewModels()
    private lateinit var adapter: AdminPartnerLocationAdapter
    private var currentItem: LocationAdminPartnerSearchResultItem? = null
    private val chatVm: ChattingViewModel by activityViewModels()
    private val vm: AdminPartnerLocationViewModel by viewModels()
    private val bridgeVm: MapBridgeViewModel by activityViewModels()

    private lateinit var mapView: MapView
    private lateinit var kakaoMap: KakaoMap
    private var mapReady = false

    // KakaoMap 현재위치 라벨
    private var myLocStyles: LabelStyles? = null
    private var myLocLabel: Label? = null

    private val partnershipVm: PartnershipViewModel by activityViewModels()

    private var pendingPartnershipId: Long? = null
    private var contractFallback: OpenContractArgs? = null

    // 마지막으로 성공한 현재 위치(재진입/되돌아가기용 캐시)
    private var lastMyLatLng: LatLng? = null

    private var poiLayer: LabelLayer? = null
    private var partnerStyles: LabelStyles? = null
    private var adminStyles: LabelStyles? = null

    private val labelToPartner = mutableMapOf<Label, PartnerOnMap>()
    private val labelToAdmin   = mutableMapOf<Label, AdminOnMap>()

    // Test
    //private val SEOUL_CITY_HALL = LatLng.from(37.4947, 126.9576)

    @Inject lateinit var authTokenLocalStore: AuthTokenLocalStore
    private val role: UserRole by lazy {
        authTokenLocalStore.getUserRoleEnum() ?: UserRole.ADMIN
    }

    private val fused by lazy { LocationServices.getFusedLocationProviderClient(requireContext()) }
    private val permLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> goToMyLocation() }

    private val DEFAULT_ZOOM = 17

    override fun initView() {
        binding.viewLocationSearchBar.setOnClickListener { navigateToSearch() }
        binding.ivLocationSearchIc.setOnClickListener { navigateToSearch() }
        binding.tvLocationHint.setOnClickListener { navigateToSearch() }
        binding.ivGoBack.setOnClickListener { goToMyLocation() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = binding.viewLocationMap

        mapView.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() { Log.d("KakaoMap", "onMapDestroy") }
                override fun onMapError(error: Exception) { Log.e("KakaoMap", "onMapError", error) }
            },
            object : KakaoMapReadyCallback() {
                override fun onMapReady(map: KakaoMap) {
                    kakaoMap = map
                    mapReady = true

                    // 클릭 리스너
                    kakaoMap.setOnLabelClickListener(object : KakaoMap.OnLabelClickListener {
                        override fun onLabelClicked(map: KakaoMap, layer: LabelLayer, label: Label): Boolean {
                            handleLabelClick(label)
                            return true
                        }
                    })

                    val locBmp = vectorToBitmap(R.drawable.ic_present_location, 24)
                    myLocStyles = map.labelManager?.addLabelStyles(
                        LabelStyles.from(
                            LabelStyle.from(locBmp).setAnchorPoint(0.5f, 1.0f)
                        )
                    )

                    // 마커 스타일 (벡터 → 비트맵, 크기 24dp)
                    val partnerBmp = vectorToBitmap(R.drawable.ic_marker, 24)
                    partnerStyles = kakaoMap.labelManager?.addLabelStyles(
                        LabelStyles.from(LabelStyle.from(partnerBmp).setAnchorPoint(0.5f, 1.0f))
                    )
                    val adminBmp = vectorToBitmap(R.drawable.ic_partner_location, 24)
                    adminStyles = kakaoMap.labelManager?.addLabelStyles(
                        LabelStyles.from(LabelStyle.from(adminBmp).setAnchorPoint(0.5f, 1.0f))
                    )

                    poiLayer = kakaoMap.labelManager?.layer

                    kakaoMap.setOnCameraMoveEndListener { _, _, _ -> requestNearbyFromCurrentViewport() }

                    kakaoMap.setOnCameraMoveStartListener { _, _ ->
                        hideItem()
                    }

                    goToMyLocation()
                    requestLocationPermissionsIfNeeded()

                    //Test
                    //moveCameraAndQuery(SEOUL_CITY_HALL.latitude, SEOUL_CITY_HALL.longitude)
                }
            }
        )

        // 목록 상태 수집 + 마커 표시 (지도 준비 안 됐으면 스킵)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.state.collect { s ->
                    if (!mapReady) return@collect
                    when (s) {
                        is AdminPartnerLocationViewModel.UiState.Idle -> Unit
                        is AdminPartnerLocationViewModel.UiState.Loading -> Log.d("UIState", "Loading…")
                        is AdminPartnerLocationViewModel.UiState.PartnerSuccess -> drawMarkersPartners(s.items)
                        is AdminPartnerLocationViewModel.UiState.AdminSuccess -> drawMarkersAdmins(s.items)
                        is AdminPartnerLocationViewModel.UiState.Fail ->
                            Log.e("UIState", "Fail: ${s.code}, ${s.message}")
                        is AdminPartnerLocationViewModel.UiState.Error ->
                            Log.e("UIState", "Error", s.t)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                partnershipVm.getPartnershipDetailUiState.collect { s ->
                    when (s) {
                        is PartnershipViewModel.PartnershipDetailUiState.Success -> {
                            val wanted = pendingPartnershipId
                            if (wanted != null && s.data.partnershipId == wanted) {
                                val fb = contractFallback
                                val data = s.data.toContractData(
                                    partnerNameFallback = fb?.partnerName, // 이름만 보강
                                    adminNameFallback   = fb?.adminName,
                                    fallbackStart       = null,            // ← 기간은 응답값 사용
                                    fallbackEnd         = null
                                )
                                PartnershipContractDialogFragment.newInstance(data)
                                    .show(parentFragmentManager, "PartnershipContractDialog")

                                pendingPartnershipId = null
                                contractFallback = null
                            }
                        }
                        is PartnershipViewModel.PartnershipDetailUiState.Fail -> {
                            Log.e("LocationFragment", "계약 상세 실패: ${s.code}, ${s.message}")
                            pendingPartnershipId = null
                            contractFallback = null
                        }
                        is PartnershipViewModel.PartnershipDetailUiState.Error -> {
                            Log.e("LocationFragment", "계약 상세 에러: ${s.message}")
                            pendingPartnershipId = null
                            contractFallback = null
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    override fun initObserver() {
        // 1) 지도 아래 캡슐 초기 데이터 바인딩 (첫 번째 아이템)
        sharedViewModel.locationList.observe(viewLifecycleOwner) { list ->
            val item = list.getOrNull(0) ?: return@observe
            currentItem = item
            val fragment = childFragmentManager
                .findFragmentById(R.id.fv_location_item) as? LocationItemFragment
            fragment?.showCapsuleInfo(item)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                bridgeVm.events.collect { ev ->
                    when (ev) {
                        is MapEvent.ShowContract -> {
                            ev.latitude?.let { lat -> ev.longitude?.let { lng -> moveCameraAndQuery(lat, lng) } }
                            contractFallback = OpenContractArgs(
                                partnershipId = ev.partnershipId,
                                latitude = ev.latitude,
                                longitude = ev.longitude,
                                partnerName = ev.partnerName,   // 이벤트에 있다면 사용
                                adminName   = ev.adminName,
                                term        = ev.term,
                                profileUrl  = ev.profileUrl
                            )
                            pendingPartnershipId = ev.partnershipId
                            partnershipVm.getPartnershipDetail(ev.partnershipId)
                        }
                    }
                }
            }
        }
    }

    private fun navigateToSearch() {
        val intent = android.content.Intent(requireContext(), LocationSearchActivity::class.java)
        searchLauncher.launch(intent)
    }

    private fun setCreateLoading(loading: Boolean) {
        binding.fvLocationItem.isEnabled = !loading
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


    // ===== 카메라 이동 & 조회 =====
    private fun moveCameraAndQuery(lat: Double, lng: Double) {
        if (!mapReady) return
        kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(LatLng.from(lat, lng), DEFAULT_ZOOM))
        requestNearbyFromCurrentViewport()
    }

    private fun centerToMyLocation(lat: Double, lng: Double) {
        lastMyLatLng = LatLng.from(lat, lng)
        showCurrentLocation(lat, lng)
        moveCameraAndQuery(lat, lng)
    }

    private fun requestNearbyFromCurrentViewport() {
        if (!mapReady || mapView.width == 0 || mapView.height == 0) return
        val vp = mapView.getViewportCorners(kakaoMap)
        val q = ViewportQuery(
            lng1 = vp.nw.longitude, lat1 = vp.nw.latitude,
            lng2 = vp.ne.longitude, lat2 = vp.ne.latitude,
            lng3 = vp.se.longitude, lat3 = vp.se.latitude,
            lng4 = vp.sw.longitude, lat4 = vp.sw.latitude
        )
        vm.load(role, q)
    }

    private fun MapView.getViewportCorners(kakaoMap: KakaoMap): Viewport {
        val w = width
        val h = height

        val nw = kakaoMap.fromScreenPoint(0, 0)!!
        val ne = kakaoMap.fromScreenPoint(w, 0)!!
        val se = kakaoMap.fromScreenPoint(w, h)!!
        val sw = kakaoMap.fromScreenPoint(0, h)!!

        return Viewport(
            minLng = listOf(nw.longitude, ne.longitude, se.longitude, sw.longitude).minOrNull() ?: 0.0,
            minLat = listOf(nw.latitude, ne.latitude, se.latitude, sw.latitude).minOrNull() ?: 0.0,
            maxLng = listOf(nw.longitude, ne.longitude, se.longitude, sw.longitude).maxOrNull() ?: 0.0,
            maxLat = listOf(nw.latitude, ne.latitude, se.latitude, sw.latitude).maxOrNull() ?: 0.0,
            nw = nw, ne = ne, se = se, sw = sw
        )
    }

    private data class Viewport(
        val minLng: Double, val minLat: Double,
        val maxLng: Double, val maxLat: Double,
        val nw: LatLng, val ne: LatLng, val se: LatLng, val sw: LatLng
    )

    // ===== 파트너 마커 (ADMIN에서 보는 목록) =====
    private fun drawMarkersPartners(items: List<PartnerOnMap>) {
        if (!mapReady) return
        val layer = poiLayer ?: kakaoMap.labelManager?.layer ?: return
        val styles = partnerStyles ?: return

        labelToPartner.clear()
        labelToAdmin.clear()
        layer.removeAll()

        items.forEach { p ->
            val label = layer.addLabel(
                LabelOptions.from(LatLng.from(p.latitude, p.longitude))
                    .setStyles(styles)
            )
            labelToPartner[label] = p
        }
    }

    // ===== 관리자(기관) 마커 (PARTNER에서 보는 목록) =====
    private fun drawMarkersAdmins(items: List<AdminOnMap>) {
        if (!mapReady) return
        val layer = poiLayer ?: kakaoMap.labelManager?.layer ?: return
        val styles = adminStyles ?: return

        labelToPartner.clear()
        labelToAdmin.clear()
        layer.removeAll()

        items.forEach { a ->
            val label = layer.addLabel(
                LabelOptions.from(LatLng.from(a.latitude, a.longitude))
                    .setStyles(styles)
            )
            labelToAdmin[label] = a
        }
    }

    // ===== 라벨 클릭 → 아래 캡슐 띄우기 =====
    private fun handleLabelClick(label: Label) {
        when (role) {
            UserRole.ADMIN -> {
                // ADMIN은 파트너 마커만 유효
                val p = labelToPartner[label] ?: return
                showCapsule(
                    LocationAdminPartnerSearchResultItem(
                        id = p.partnerId,                   // 상대(파트너) id
                        shopName = p.shopName,
                        address = p.address ?: "",
                        partnered = p.partnered,
                        partnershipId = p.partnershipId,
                        partnershipStartDate = p.partnershipStartDate,
                        partnershipEndDate = p.partnershipEndDate,
                        latitude = p.latitude,
                        longitude = p.longitude,
                        paperId = null,
                        profileUrl = p.profileUrl,
                        phoneNumber = p.phoneNum,
                        term = if (!p.partnershipStartDate.isNullOrBlank() && !p.partnershipEndDate.isNullOrBlank())
                            "${p.partnershipStartDate} ~ ${p.partnershipEndDate}"
                        else null
                    )
                )
            }

            UserRole.PARTNER -> {
                // PARTNER는 관리자 마커만 유효
                val a = labelToAdmin[label] ?: return
                showCapsule(
                    LocationAdminPartnerSearchResultItem(
                        id = a.adminId,                     // 상대(관리자) id
                        shopName = a.name,
                        address = a.address ?: "",
                        partnered = a.partnered,
                        partnershipId = a.partnershipId,
                        partnershipStartDate = a.partnershipStartDate,
                        partnershipEndDate = a.partnershipEndDate,
                        latitude = a.latitude,
                        longitude = a.longitude,
                        paperId = null,
                        profileUrl = a.profileUrl,
                        phoneNumber = a.phoneNum,
                        term = if (!a.partnershipStartDate.isNullOrBlank() && !a.partnershipEndDate.isNullOrBlank())
                            "${a.partnershipStartDate} ~ ${a.partnershipEndDate}"
                        else null
                    )
                )
            }

            else -> return
        }
    }

    private fun showCapsule(item: LocationAdminPartnerSearchResultItem) {
        val frag = childFragmentManager.findFragmentById(R.id.fv_location_item) as? LocationItemFragment
            ?: LocationItemFragment().also {
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.fv_location_item, it)
                    .commitNowAllowingStateLoss()
            }
        currentItem = item
        frag.showCapsuleInfo(item)
        binding.fvLocationItem.apply {
            visibility = View.VISIBLE
            bringToFront()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapReady = false
        if (::mapView.isInitialized) mapView.removeAllViews()
    }
    override fun onResume() { super.onResume() ;if (::mapView.isInitialized) mapView.resume() }
    override fun onPause() { super.onPause(); if (::mapView.isInitialized) mapView.pause() }

    // ===== 벡터 → 비트맵 =====
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

    private fun hideItem() {
        binding.fvLocationItem.visibility = View.GONE
    }

    private fun showCurrentLocation(lat: Double, lng: Double) {
        if (!mapReady || kakaoMap == null) return
        val styles = myLocStyles ?: return
        val layer = kakaoMap!!.labelManager?.layer ?: return

        // 이전 현재위치 라벨 제거 후 새로 추가(업데이트 느낌)
        myLocLabel?.remove()
        myLocLabel = layer.addLabel(
            LabelOptions.from(LatLng.from(lat, lng)).setStyles(styles)
        )
    }

    private fun showContractDialog(args: OpenContractArgs) {
        val start = args.term?.split("~")?.getOrNull(0)?.trim().orEmpty()
        val end   = args.term?.split("~")?.getOrNull(1)?.trim().orEmpty()

        val data = com.example.assu_fe_app.data.dto.partnership.PartnershipContractData(
            partnerName = args.partnerName ?: "-",
            adminName   = args.adminName ?: "-",
            periodStart = start,
            periodEnd   = end,
            options     = emptyList() // 지금은 args-only 플로우이므로 옵션은 비움
        )

        PartnershipContractDialogFragment
            .newInstance(data)
            .show(parentFragmentManager, "contractDialog")
    }

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

    private val searchLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val args = result.data?.getSerializableExtra("open_contract_args") as? OpenContractArgs
                    ?: return@registerForActivityResult

                if (args.latitude != null && args.longitude != null) {
                    moveCameraAndQuery(args.latitude, args.longitude)
                }

                //  fallback 저장 + 상세조회 호출
                contractFallback = args
                pendingPartnershipId = args.partnershipId
                partnershipVm.getPartnershipDetail(args.partnershipId)
                Log.d("LocationFragment", "검색 선택: pid=${args.partnershipId}, 상세조회 호출")
            }
        }
}