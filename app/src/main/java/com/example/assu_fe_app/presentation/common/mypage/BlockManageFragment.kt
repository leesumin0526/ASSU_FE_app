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
        // ViewModel의 getBlockListState StateFlow를 관찰하여 UI를 업데이트합니다.
        // Fragment의 생명주기에 맞게 안전하게 데이터를 수집하기 위해 repeatOnLifecycle를 사용합니다.
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getBlockListState.collect { state ->

                    when (state) {
                        is ChattingViewModel.GetBlockListUiState.Loading -> {
                            // 로딩 중일 때 ProgressBar를 보여줍니다.
                        }
                        is ChattingViewModel.GetBlockListUiState.Success -> {
                            val blockList = state.data
                            // 1. 총 차단 인원 수를 TextView에 업데이트합니다.
                            // state.data.size를 통해 리스트의 총 개수를 구합니다.
                            binding.tvBlockCount.text = blockList.size.toString()

                            // 2. 어댑터에 새로운 리스트를 전달하여 RecyclerView를 업데이트합니다.
                            blockListAdapter.submitList(blockList)
                        }
                        is ChattingViewModel.GetBlockListUiState.Fail -> {
                            // API 호출은 성공했으나, 서버에서 실패 응답을 보냈을 때
                            Toast.makeText(requireContext(), "오류가 발생했습니다: ${state.message}", Toast.LENGTH_SHORT).show()
                        }
                        is ChattingViewModel.GetBlockListUiState.Error -> {
                            // 네트워크 오류 등 예외가 발생했을 때
                            Toast.makeText(requireContext(), "네트워크 오류: ${state.message}", Toast.LENGTH_SHORT).show()
                        }
                        is ChattingViewModel.GetBlockListUiState.Idle -> {
                            // 초기 상태. 아무것도 하지 않음.
                        }
                    }
                }
            }
        }

        // TODO: 차단 해제 상태를 관찰하는 Observer를 추가해야 합니다.
        // viewModel.unblockUserState.collect { ... }
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
            Toast.makeText(requireContext(), "${user.name}님을 차단 해제합니다.", Toast.LENGTH_SHORT).show()
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