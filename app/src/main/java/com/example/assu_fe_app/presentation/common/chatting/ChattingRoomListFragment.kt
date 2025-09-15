package com.example.assu_fe_app.presentation.common.chatting

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentChattingListBinding
import com.example.assu_fe_app.domain.model.chatting.GetChattingRoomListModel
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.chatting.adapter.ChattingRoomListAdapter
import com.example.assu_fe_app.ui.chatting.ChattingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChattingRoomListFragment :BaseFragment<FragmentChattingListBinding> (R.layout.fragment_chatting_list){

    private val viewModel: ChattingViewModel by viewModels()

    // 클릭 시 액션 결정
    private val adapter by lazy {
        ChattingRoomListAdapter(onItemClick = ::onRoomClick)
    }

    override fun initView() {
        // RecyclerView 세팅
        binding.rvChattingRoomList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ChattingRoomListFragment.adapter
            setHasFixedSize(true)
        }

        // ✅ 어댑터에 데이터 들어오는지 로그 찍기
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                Log.d("recyclerView", "inserted: start=$positionStart, count=$itemCount, total=${adapter.itemCount}")
            }

            override fun onChanged() {
                Log.d("recyclerView", "changed total=${adapter.itemCount}")
            }
        })
        viewModel.getChattingRoomList()
    }



    override fun initObserver() {
        // uiState 수집
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getChattingRoomListState.collect { uiState ->
                    when (uiState) {
                        is ChattingViewModel.GetChattingRoomListUiState.Loading -> {
                        }
                        is ChattingViewModel.GetChattingRoomListUiState.Success -> {
                            val isEmpty = uiState.data.isEmpty()
                            adapter.submitList(uiState.data)

                            binding.layoutAdminChattingNoHistoryInfo.isGone = !isEmpty
                            binding.rvChattingRoomList.isVisible = true
                            Log.i("ChattingRoomListFragment", "채팅방 리스트 불러오기 성공")
                        }
                        is ChattingViewModel.GetChattingRoomListUiState.Fail -> {
                            Toast.makeText(requireContext(), "채팅방 리스트 불러오기 실패(${uiState.code})", Toast.LENGTH_SHORT).show()
                            Log.e("ChattingRoomListFragment", "Fail code=${uiState.code}, msg=${uiState.message}")
                        }
                        is ChattingViewModel.GetChattingRoomListUiState.Error -> {
                            Toast.makeText(requireContext(), "에러: ${uiState.message}", Toast.LENGTH_SHORT).show()
                            Log.e("ChattingRoomListFragment", "Error: ${uiState.message}")
                        }
                        is ChattingViewModel.GetChattingRoomListUiState.Idle -> {
                            Log.d("ChattingRoomListFragment", "Idle 상태")
                        }
                        else -> {
                            // 이건 뭐로하지
                        }
                    }
                }
            }
        }
    }

    private fun onRoomClick(item: GetChattingRoomListModel) {
        binding.rvChattingRoomList.isEnabled = false
        binding.rvChattingRoomList.postDelayed({binding.rvChattingRoomList.isEnabled = true}, 500)

        val intent = Intent(requireContext(), ChattingActivity::class.java).apply {
            putExtra("roomId", item.roomId)
            putExtra("opponentId", item.opponentId)
            putExtra("opponentName", item.opponentName)
            putExtra("opponentProfileImage", item.opponentProfileImage)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        // ✅ 프래그먼트가 사용자에게 보일 때마다 목록 새로고침
        viewModel.getChattingRoomList()
    }
}