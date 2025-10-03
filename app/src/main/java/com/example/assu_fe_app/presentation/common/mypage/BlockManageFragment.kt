package com.example.assu_fe_app.presentation.common.mypage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentBlockManageBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.mypage.adapter.BlockListAdapter
import com.example.assu_fe_app.ui.chatting.ChattingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BlockManageFragment : BaseFragment<FragmentBlockManageBinding>(R.layout.fragment_block_manage) {

    // ViewModel 주입: GetBlockListModel이 아닌, 이 Fragment를 관리할 ViewModel을 주입해야 합니다.
    // 이름을 'BlockManageViewModel'이라고 가정하겠습니다.
    private val viewModel: ChattingViewModel by viewModels()

    // RecyclerView 어댑터 선언
    private lateinit var blockListAdapter: BlockListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Fragment의 View가 생성된 후, API를 통해 차단 목록을 가져오도록 ViewModel에 요청합니다.
        viewModel.getBlockList()
    }

    override fun initObserver() {
        observeBlockList()
        observeUnblockState() // 차단 해제 상태를 관찰하는 함수 호출
    }

    private fun observeBlockList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getBlockListState.collect { state ->
                    when (state) {
                        is ChattingViewModel.GetBlockListUiState.Loading -> {

                        }
                        is ChattingViewModel.GetBlockListUiState.Success -> {
                            val blockList = state.data
                            binding.tvBlockCount.text = blockList.size.toString()
                            blockListAdapter.submitList(blockList)
                        }
                        is ChattingViewModel.GetBlockListUiState.Fail -> {
                            Toast.makeText(requireContext(), "오류가 발생했습니다: ${state.message}", Toast.LENGTH_SHORT).show()
                        }
                        is ChattingViewModel.GetBlockListUiState.Error -> {
                            Toast.makeText(requireContext(), "네트워크 오류: ${state.message}", Toast.LENGTH_SHORT).show()
                        }
                        is ChattingViewModel.GetBlockListUiState.Idle -> {
                            // 초기 상태
                        }
                    }
                }
            }
        }
    }

    // 새로 추가된 차단 해제 상태 관찰 로직
    private fun observeUnblockState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.unblockOpponentState.collect { state ->
                    when (state) {
                        is ChattingViewModel.UnblockOpponentUiState.Loading -> {

                        }
                        is ChattingViewModel.UnblockOpponentUiState.Success -> {
                            Toast.makeText(requireContext(), "차단이 해제되었습니다.", Toast.LENGTH_SHORT).show()

                            viewModel.getBlockList()
                             viewModel.resetUnblockOpponentState()
                        }
                        is ChattingViewModel.UnblockOpponentUiState.Fail -> {
                            Toast.makeText(requireContext(), "차단 해제에 실패했습니다: ${state.message}", Toast.LENGTH_SHORT).show()
                        }
                        is ChattingViewModel.UnblockOpponentUiState.Error -> {
                            Toast.makeText(requireContext(), "오류 발생: ${state.message}", Toast.LENGTH_SHORT).show()
                        }
                        is ChattingViewModel.UnblockOpponentUiState.Idle -> {
                            // 초기 상태
                        }
                    }
                }
            }
        }
    }

    override fun initView() {
        // 1. 어댑터를 초기화합니다.
        setupAdapter()

        // 2. RecyclerView를 설정합니다.
        setupRecyclerView()

        // 3. 뒤로가기 버튼 클릭 리스너를 설정합니다.
        binding.btnPendingBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupAdapter() {
        // BlockListAdapter를 생성하면서, '차단 해제' 버튼 클릭 시 실행될 람다 함수를 전달합니다.
        blockListAdapter = BlockListAdapter { user ->
            // TODO: 차단 해제 확인 다이얼로그를 띄우고, '확인' 시 아래 로직을 실행하도록 구현

            // ViewModel에 차단 해제 API를 호출하도록 요청합니다.
            // viewModel.unblockUser(user.memberId)
            viewModel.unblockOpponent(user.memberId)
        }
    }

    private fun setupRecyclerView() {
        binding.rvBlockList.apply {
            // 어댑터를 RecyclerView에 연결합니다.
            adapter = blockListAdapter
            // 아이템을 세로로 나열하도록 LinearLayoutManager를 설정합니다.
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}