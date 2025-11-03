package com.ssu.assu.presentation.user.home

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssu.assu.R
import com.ssu.assu.databinding.ActivityUserMyPartnershipListBinding
import com.ssu.assu.presentation.base.BaseActivity
import com.ssu.assu.presentation.user.home.adapter.UserPartnershipListAdapter
import com.ssu.assu.ui.user.UserHomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserMyPartnershipListActivity :
    BaseActivity<ActivityUserMyPartnershipListBinding>(R.layout.activity_user_my_partnership_list) {

    private val vm: UserHomeViewModel by viewModels()
    private lateinit var partnershipAdapter: UserPartnershipListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.getUsableProposalList(true)
    }

    override fun initView() {

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val extraPaddingTop = 3 // 8dp 추가
            v.setPadding(
                systemBars.left,
                systemBars.top + extraPaddingTop.dpToPx(v.context),
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
        binding.ivBtnBack.setOnClickListener {
            finish()
        }

        partnershipAdapter = UserPartnershipListAdapter()
        binding.rvPartnerList.apply {
            adapter = partnershipAdapter
            layoutManager = LinearLayoutManager(this@UserMyPartnershipListActivity)
        }
    }

    override fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.getUsableProposalState.collect { state ->
                    when (state) {
                        is UserHomeViewModel.GetUsableProposalUiState.Loading -> {
                            // TODO: 로딩 인디케이터 표시
                        }

                        is UserHomeViewModel.GetUsableProposalUiState.Success -> {
                            // 5. 어댑터에 데이터 리스트 제출
                            partnershipAdapter.submitList(state.data)
                            // 6. 상단 제휴 개수 텍스트 업데이트
                            binding.tvAdminCount.text = state.data.size.toString()
                        }

                        is UserHomeViewModel.GetUsableProposalUiState.Fail -> {
                        }

                        is UserHomeViewModel.GetUsableProposalUiState.Error -> {
                        }

                        is UserHomeViewModel.GetUsableProposalUiState.Idle -> {
                            //
                        }
                    }
                }
            }
        }
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

}