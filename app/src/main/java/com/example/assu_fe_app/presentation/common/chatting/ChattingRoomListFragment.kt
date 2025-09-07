package com.example.assu_fe_app.presentation.common.chatting

import android.util.Log
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
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
        viewModel.getChattingRoomList()
    }



    override fun initObserver() {
        // uiState 수집
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getChattingRoomListState.collect { uiState ->
                    when (uiState) {
                        is ChattingViewModel.GetChattingRoomListUiState.Loading -> {
                            Toast.makeText(requireContext(), "로딩 중…", Toast.LENGTH_SHORT).show()
                        }
                        is ChattingViewModel.GetChattingRoomListUiState.Success -> {
                            val isEmpty = uiState.data.isEmpty()
                            adapter.submitList(uiState.data)
                            binding.layoutAdminChattingNoHistoryInfo.isVisible = isEmpty
                            binding.rvChattingRoomList.isVisible = !isEmpty
                            Toast.makeText(requireContext(), "채팅방 리스트 불러오기 성공", Toast.LENGTH_SHORT).show()
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

    }
}