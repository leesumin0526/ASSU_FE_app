package com.ssu.assu.presentation.common.chatting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssu.assu.R
import com.ssu.assu.data.local.AuthTokenLocalStoreImpl
import com.ssu.assu.databinding.FragmentChattingListBinding
import com.ssu.assu.domain.model.chatting.GetChattingRoomListModel
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.presentation.common.chatting.adapter.ChattingRoomListAdapter
import com.ssu.assu.ui.chatting.ChattingListViewModel
import com.ssu.assu.ui.chatting.ChattingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class ChattingRoomListFragment :BaseFragment<FragmentChattingListBinding> (R.layout.fragment_chatting_list){

    private val viewModel: ChattingListViewModel by viewModels()
    private val chattingViewModel: ChattingViewModel by viewModels()
    private val authTokenLocalStoreImpl by lazy {
        AuthTokenLocalStoreImpl(requireContext())
    }

    // 클릭 시 액션 결정
    private val adapter by lazy {
        ChattingRoomListAdapter(onItemClick = ::onRoomClick, authTokenLocalStoreImpl)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ▼▼▼ 4. onViewCreated에서 초기 데이터 로드 ▼▼▼
        viewModel.getChattingRoomList()
    }

    override fun initView() {
        // RecyclerView 세팅
        binding.rvChattingRoomList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ChattingRoomListFragment.adapter
            setHasFixedSize(true)
        }
    }

    override fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.chatRooms.collect { roomList ->
                    val isEmpty = roomList.isEmpty()
                    adapter.submitList(roomList)

                    binding.layoutAdminChattingNoHistoryInfo.isGone = !isEmpty
                    binding.rvChattingRoomList.isVisible = !isEmpty // 리스트가 비어있지 않을 때만 보이도록 수정
                    Log.i("ChattingListFragment", "Chat room list updated with ${roomList.size} items.")
                }
            }
        }
    }

    // ▼▼▼ 3. 실시간 구독/해제 로직을 위한 onStart, onStop 추가 ▼▼▼
    override fun onStart() {
        super.onStart()
        // 화면이 사용자에게 보일 때, 실시간 업데이트 구독 시작
        viewModel.getChattingRoomList()
        viewModel.subscribeToUserUpdates()
    }

    override fun onStop() {
        super.onStop()
        // 화면이 가려지면, 리소스 절약을 위해 구독 해제
        viewModel.unsubscribeFromUserUpdates()
    }

    private fun onRoomClick(item: GetChattingRoomListModel) {
        binding.rvChattingRoomList.isEnabled = false
        binding.rvChattingRoomList.postDelayed({binding.rvChattingRoomList.isEnabled = true}, 500)
        val safeName = if (item.opponentId == -1L) {
            "알 수 없음"
        } else {
            item.opponentName
        }

        val opponentProfile = if (item.opponentId == -1L) {
            if (authTokenLocalStoreImpl.getUserRole() == "PARTNER") {
                // TODO img_admin으로 바꾸기
                R.drawable.img_partner
            } else {
                R.drawable.img_partner
            }
        } else {
            item.opponentProfileImage
        }

        // 코루틴 스코프에서 비동기 작업 실행
        viewLifecycleOwner.lifecycleScope.launch {
            // 로딩 UI 표시 (예: Toast)
//            Toast.makeText(requireContext(), "제휴 정보 확인 중...", Toast.LENGTH_SHORT).show()

            // ViewModel의 suspend 함수를 호출하고 결과를 기다림
            val status = chattingViewModel.checkPartnershipStatus(authTokenLocalStoreImpl.getUserRole(), item.opponentId)

            // API 호출 결과에 따라 분기 처리
            if (status != null) {
                // 성공: Intent에 모든 정보를 담아 Activity 시작
                val intent = Intent(requireContext(), ChattingActivity::class.java).apply {
                    putExtra("roomId", item.roomId)
                    putExtra("opponentName", item.opponentName)
                    putExtra("opponentProfileImage", item.opponentProfileImage)
                    putExtra("partnershipStatus", status)
                    putExtra("opponentId", item.opponentId)
                    putExtra("phoneNumber", item.phoneNumber)
                }
                startActivity(intent)
            } else {
                // 실패: 에러 메시지 표시
                Toast.makeText(requireContext(), "제휴 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}