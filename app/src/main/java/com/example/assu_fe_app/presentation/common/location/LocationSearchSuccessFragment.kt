package com.example.assu_fe_app.presentation.common.location

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.UserRole
import com.example.assu_fe_app.data.dto.chatting.request.CreateChatRoomRequestDto
import com.example.assu_fe_app.data.dto.location.LocationAdminPartnerSearchResultItem
import com.example.assu_fe_app.data.dto.location.LocationSearchItem
import com.example.assu_fe_app.data.dto.partnership.OpenContractArgs
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import com.example.assu_fe_app.databinding.FragmentLocationSearchSuccessBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.location.adapter.AdminPartnerLocationAdapter
import com.example.assu_fe_app.presentation.common.location.adapter.LocationSharedViewModel
import com.example.assu_fe_app.ui.chatting.ChattingViewModel
import com.example.assu_fe_app.ui.map.AdminPartnerKeyWordSearchViewModel
import com.example.assu_fe_app.ui.map.MapBridgeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LocationSearchSuccessFragment :
    BaseFragment<FragmentLocationSearchSuccessBinding>(R.layout.fragment_location_search_success) {
    @Inject lateinit var authTokenLocalStore: AuthTokenLocalStore

    private val sharedViewModel: LocationSharedViewModel by viewModels()
    private val searchViewModel : AdminPartnerKeyWordSearchViewModel by activityViewModels()
    private val chatVm: ChattingViewModel by activityViewModels()

    private var lastItem: LocationAdminPartnerSearchResultItem? = null
    private lateinit var adapter: AdminPartnerLocationAdapter
    private lateinit var role: UserRole

    private var phoneNum: String? = null
    private var navigated = false

    override fun onResume() {
        super.onResume()
        navigated = false
    }

    override fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatVm.createRoomState.collect { state ->
                    when (state) {
                        is ChattingViewModel.CreateRoomUiState.Loading -> Unit
                        is ChattingViewModel.CreateRoomUiState.Success -> {
                            val roomId = state.data.roomId
                            val displayName = when (role) {
                                UserRole.ADMIN   -> state.data.adminViewName
                                UserRole.PARTNER -> state.data.partnerViewName
                                else             -> state.data.adminViewName
                            }
                            val opponentId = lastItem?.id ?: -1L

                            if (!navigated) {
                                navigated = true
                                val intent = Intent(
                                    requireContext(),
                                    com.example.assu_fe_app.presentation.common.chatting.ChattingActivity::class.java
                                ).apply {
                                    putExtra("roomId", roomId)
                                    putExtra("opponentName", displayName)
                                    putExtra("opponentId", opponentId)
                                    putExtra("entryMessage", "'문의하기' 버튼을 통해 이동했습니다.")
                                    putExtra("phoneNumber", phoneNum)
                                }
                                startActivity(intent)
                            }
                            chatVm.resetCreateState()
                            phoneNum = null
                        }
                        is ChattingViewModel.CreateRoomUiState.Fail,
                        is ChattingViewModel.CreateRoomUiState.Error -> {
                            chatVm.resetCreateState()
                            phoneNum = null
                        }
                        else -> Unit
                    }
                }
            }
        }

        searchViewModel.contentList.observe(viewLifecycleOwner){ contentList ->
            adapter.submitList(contentList)
            Log.d("LocationSearchSuccessFragment", "관찰된 데이터: $contentList")
            binding.tvLocationSearchSuccessCount.text = contentList.size.toString()

        }
        searchViewModel.isEmptyList.observe(viewLifecycleOwner){ isEmpty ->
            if(isEmpty){
                binding.tvLocationSearchSuccessTitle.visibility = View.GONE
                binding.tvLocationSearchSuccessCount.visibility= View.GONE
                binding.rvLocationSearchSuccess.visibility = View.GONE
                binding.llLocationAdminPartnerItemEmpty.visibility=  View.VISIBLE
            }
            else {
                binding.tvLocationSearchSuccessTitle.visibility = View.VISIBLE
                binding.tvLocationSearchSuccessCount.visibility= View.VISIBLE
                binding.rvLocationSearchSuccess.visibility = View.VISIBLE
                binding.llLocationAdminPartnerItemEmpty.visibility=  View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatVm.createRoomState.collect { state ->
                    when (state) {
                        is ChattingViewModel.CreateRoomUiState.Idle -> Log.d("Chatting", "Loading")
                        is ChattingViewModel.CreateRoomUiState.Loading -> Log.d("Chatting", "Loading")
                        is ChattingViewModel.CreateRoomUiState.Success -> {

                            val roomId = state.data.roomId
                            val displayName = when (role) {
                                UserRole.ADMIN   -> state.data.adminViewName
                                UserRole.PARTNER -> state.data.partnerViewName
                                else             -> state.data.adminViewName
                            }
                            val opponentId = lastItem?.id ?: -1L

                            val intent = Intent(requireContext(), com.example.assu_fe_app.presentation.common.chatting.ChattingActivity::class.java).apply {
                                putExtra("roomId", roomId)
                                putExtra("opponentName", displayName)
                                putExtra("opponentId", opponentId)
                                putExtra("entryMessage", "'문의하기' 버튼을 통해 이동했습니다.")
                                putExtra("phoneNum", phoneNum)
                            }
                            startActivity(intent)

                            chatVm.resetCreateState()
                        }
                        is ChattingViewModel.CreateRoomUiState.Fail -> {
                            chatVm.resetCreateState()
                        }
                        is ChattingViewModel.CreateRoomUiState.Error -> {
                            chatVm.resetCreateState()
                        }
                    }
                }
            }
        }
    }

    override fun initView() {
        role = authTokenLocalStore.getUserRoleEnum() ?: UserRole.ADMIN
        initAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initAdapter() {
        val myName = authTokenLocalStore.getUserName()

        adapter = AdminPartnerLocationAdapter(
            role = role,
            myName = myName,
            onOpenContract = { args ->
                // 제휴 중: 계약서 보기 → 결과 반환해서 현재 검색 액티비티 종료
                val data = Intent().apply {
                    putExtra("open_contract_args", args) // Serializable/Parcelable
                }
                requireActivity().setResult(android.app.Activity.RESULT_OK, data)
                requireActivity().finish()
            },
            onAskChat = { item ->
                lastItem = item
                // 제휴 아님: 문의하기 → 채팅방 생성
                val opponentId = item.id ?: run {
                    return@AdminPartnerLocationAdapter
                }

                phoneNum = item.phoneNumber

                val req = when (role) {
                    UserRole.ADMIN -> {
                        val adminId   = authTokenLocalStore.getUserId()
                        val partnerId = opponentId
                        CreateChatRoomRequestDto(
                            adminId = adminId, partnerId = partnerId
                        )
                    }
                    UserRole.PARTNER -> {
                        val adminId   = opponentId
                        val partnerId = authTokenLocalStore.getUserId()
                        CreateChatRoomRequestDto(
                            adminId = adminId, partnerId = partnerId
                        )
                    }
                    else -> return@AdminPartnerLocationAdapter
                }

                // ChattingViewModel 로 채팅방 생성
                chatVm.createRoom(req)
            }
        )

        binding.rvLocationSearchSuccess.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@LocationSearchSuccessFragment.adapter
        }

    }

}