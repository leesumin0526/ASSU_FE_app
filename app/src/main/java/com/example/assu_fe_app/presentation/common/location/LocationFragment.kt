package com.example.assu_fe_app.presentation.common.location

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.chatting.request.CreateChatRoomRequestDto
import com.example.assu_fe_app.data.dto.location.LocationAdminPartnerSearchResultItem
import com.example.assu_fe_app.databinding.FragmentLoactionBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.chatting.ChattingActivity
import com.example.assu_fe_app.presentation.common.location.adapter.AdminPartnerLocationAdapter
import com.example.assu_fe_app.presentation.common.location.adapter.LocationSharedViewModel
import com.example.assu_fe_app.ui.chatting.ChattingViewModel
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class LocationFragment :
    BaseFragment<FragmentLoactionBinding>(R.layout.fragment_loaction) {
    private val sharedViewModel: LocationSharedViewModel by activityViewModels()
    private lateinit var adapter: AdminPartnerLocationAdapter
    private var currentItem: LocationAdminPartnerSearchResultItem? = null
    private lateinit var mapView: MapView
    private lateinit var kakaoMap : KakaoMap

    // 뷰모델 주입
    private val vm: ChattingViewModel by viewModels()

    override fun initView() {
        val dummyList = listOf(
            LocationAdminPartnerSearchResultItem(2,"IT대 학생회", "서울 동작구 사당로 36-1 서정캐슬", true, 1,"2025.02.24 ~ 2025.06.15"),
            LocationAdminPartnerSearchResultItem(2,"역전할머니맥주 숭실대점2", "서울 동작구 사당로 36-1 서정캐슬", false, null,"")
        )
        sharedViewModel.locationList.value = dummyList
        adapter = AdminPartnerLocationAdapter()
        adapter.submitList(dummyList)

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

            // TODO: 여기서 id 불러오는 방법 바꾸기
            val storeId =1L
            val partnerId = 5L

            val entryMessage = if (item.isPartnered) {
                "'제휴 계약서 보기' 버튼을 통해 이동했습니다."
            } else {
                "'문의하기' 버튼을 통해 이동했습니다.이거야?"
            }

            vm.createRoom(
                CreateChatRoomRequestDto(
                    adminId = storeId,
                    partnerId = partnerId)
            )
            binding.root.tag = entryMessage
        //            val intent = Intent(context, ChattingActivity::class.java)

//            val message = if (item.isPartnered) {
//                "'제휴 계약서 보기' 버튼을 통해 이동했습니다."
//            } else {
//                "'문의하기' 버튼을 통해 이동했습니다.이거야?"
//            }
//
//            intent.putExtra("entryMessage", message)
//            context.startActivity(intent)
        }
        mapView = MapView(requireContext())
        val mapContainer = binding.root.findViewById<ViewGroup>(R.id.view_location_map)
        mapContainer.addView(mapView)
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

        // ViewModel 상태 수집
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.createRoomState.collect { state ->
                    when (state) {
                        is ChattingViewModel.CreateRoomUiState.Idle -> {
                            setCreateLoading(false)
                        }

                        is ChattingViewModel.CreateRoomUiState.Loading -> {
                            setCreateLoading(true)
                        }

                        is ChattingViewModel.CreateRoomUiState.Success -> {
                            setCreateLoading(false)
                            // 채팅방 화면으로 이동
                            val intent =
                                Intent(requireContext(), ChattingActivity::class.java).apply {
                                    putExtra("roomId", state.data.roomId)
                                    (binding.root.tag as? String)?.let {
                                        putExtra(
                                            "entryMessage",
                                            it
                                        )
                                    }
                                }
                            startActivity(intent)
                            vm.resetCreateState()
                        }

                        is ChattingViewModel.CreateRoomUiState.Fail -> {
                            setCreateLoading(false)
                            Toast.makeText(
                                requireContext(),
                                "채팅방 생성 실패(${state.code}) ${state.message ?: ""}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is ChattingViewModel.CreateRoomUiState.Error -> {
                            setCreateLoading(false)
                            Toast.makeText(
                                requireContext(),
                                "오류: ${state.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
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

    private fun setCreateLoading(loading: Boolean) {
        // 캡슐 뷰 클릭 방지
        binding.fvLocationItem.isEnabled = !loading
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