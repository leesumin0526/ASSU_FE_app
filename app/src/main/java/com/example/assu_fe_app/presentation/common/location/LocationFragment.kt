package com.example.assu_fe_app.presentation.common.location

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.location.LocationAdminPartnerSearchResultItem
import com.example.assu_fe_app.databinding.FragmentLoactionBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.chatting.ChattingActivity
import com.example.assu_fe_app.presentation.common.location.adapter.AdminPartnerLocationAdapter
import com.example.assu_fe_app.presentation.common.location.adapter.LocationSharedViewModel
import com.example.assu_fe_app.presentation.user.review.store.UserReviewStoreActivity
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView

class LocationFragment :
    BaseFragment<FragmentLoactionBinding>(R.layout.fragment_loaction) {
    private val sharedViewModel: LocationSharedViewModel by activityViewModels()
    private lateinit var adapter: AdminPartnerLocationAdapter
    private var currentItem: LocationAdminPartnerSearchResultItem? = null
    private lateinit var mapView: MapView
    private lateinit var kakaoMap : KakaoMap

    override fun initView() {
        val dummyList = listOf(
            LocationAdminPartnerSearchResultItem("역전할머니맥주 숭실대점1", "서울 동작구 사당로 36-1 서정캐슬", true, "2025.02.24 ~ 2025.06.15"),
            LocationAdminPartnerSearchResultItem("역전할머니맥주 숭실대점2", "서울 동작구 사당로 36-1 서정캐슬", false, "")
        )
        sharedViewModel.locationList.value = dummyList
        adapter = AdminPartnerLocationAdapter(dummyList)

        binding.viewLocationSearchBar.setOnClickListener {
            navigateToSearch()
        }
        binding.ivLocationSearchIc.setOnClickListener {
            navigateToSearch()
        }
        binding.tvLocationHint.setOnClickListener {
            navigateToSearch()
        }

        binding.viewLocationMap.setOnClickListener{
            binding.fvLocationItem.visibility = View.VISIBLE
        }

        binding.fvLocationItem.setOnClickListener {
            val item = currentItem ?: return@setOnClickListener
            val context = it.context
            val intent = Intent(context, ChattingActivity::class.java)

            val message = if (item.isPartnered) {
                "'제휴 계약서 보기' 버튼을 통해 이동했습니다."
            } else {
                "'문의하기' 버튼을 통해 이동했습니다."
            }

            intent.putExtra("entryMessage", message)
            context.startActivity(intent)
        }

//        mapView = MapView(requireContext())

//        val mapContainer = binding.root.findViewById<ViewGroup>(R.id.view_location_map)
//        mapContainer.addView(mapView)
//



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = binding.viewLocationMap
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API가 정상적으로 종료될 때 호출
                Log.d("KakaoMap", "onMapDestroy: ")
            }

            override fun onMapError(error: Exception) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출
                Log.e("KakaoMap", "onMapError: ", error)
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                // 정상적으로 인증이 완료되었을 때 호출
                // KakaoMap 객체를 얻어 옵니다.
                kakaoMap = map
            }
        })
    }

    override fun initObserver() {
        sharedViewModel.locationList.observe(viewLifecycleOwner) { list ->
            val item = list.getOrNull(1) ?: return@observe
            currentItem = item

            val fragment = childFragmentManager.findFragmentById(R.id.fv_location_item) as? LocationItemFragment
            fragment?.showCapsuleInfo(item)
        }
    }

    private fun navigateToSearch() {
        val intent = Intent(requireContext(), LocationSearchActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        mapView.removeAllViews()
        if (::mapView.isInitialized) {
            mapView.removeAllViews()
        }
    }


    override fun onStop() {
        super.onStop()
        Log.d("KakaoMapLifecycle", "onStop: Fragment stopped")
        // onPause에서 이미 mapView.pause()를 호출하고 있으므로,
        // Kakao SDK에서 명시적으로 onStop에서 호출해야 하는 API가 없다면 추가 작업은 필요 없을 수 있습니다.
        // SDK 문서 확인 필요.
    }

    override fun onResume() {
        super.onResume()
        // Fragment가 화면에 다시 나타날 때 MapView를 다시 시작합니다.
        // MapView가 초기화된 후에만 start()를 호출해야 합니다.
        if (::mapView.isInitialized) {
            Log.d("KakaoMapLifecycle", "onResume: Starting MapView")
            mapView.resume() // Kakao SDK에 resume()이 있다면 사용, 없다면 start()
        }
    }

    override fun onPause() {
        super.onPause()
        // Fragment가 화면에서 사라질 때 MapView를 일시 중지합니다.
        // MapView가 초기화된 후에만 pause()를 호출해야 합니다.
        if (::mapView.isInitialized) {
            Log.d("KakaoMapLifecycle", "onPause: Pausing MapView")
            mapView.pause() // Kakao SDK에 pause()가 있다면 사용, 없다면 stop() 또는 API 문서 참고
        }
    }

}