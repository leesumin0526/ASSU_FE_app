package com.assu.app.presentation.user.home

import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assu.app.R
import com.assu.app.databinding.FragmentMyPartnershipDetailsBinding
import com.assu.app.presentation.base.BaseFragment
import com.assu.app.presentation.user.dashboard.adapter.ServiceRecordAdapter
import com.assu.app.ui.usage.UnreviewedUsageViewModel
import com.assu.app.ui.user.UserHomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MyPartnershipDetailsFragment :
    BaseFragment<FragmentMyPartnershipDetailsBinding>(R.layout.fragment_my_partnership_details) {

    private lateinit var serviceRecordAdapter: ServiceRecordAdapter
    private val viewModel : UnreviewedUsageViewModel by viewModels()
    private val stampViewModel: UserHomeViewModel by activityViewModels()


    private lateinit var stampViews: List<ImageView>

    override fun initObserver() {
        viewModel.usageList.observe(viewLifecycleOwner) { records ->
            serviceRecordAdapter.setData(records)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            stampViewModel.stampState.collect { state ->
                when (state) {
                    is UserHomeViewModel.StampUiState.Success -> {
                        updateStampDisplay(state.stampCount)
                    }
                    is UserHomeViewModel.StampUiState.Error -> {
                        updateStampDisplay(0) // 에러 시 0개로 표시
                    }
                    // Loading, Idle 상태는 필요에 따라 처리
                    else -> {}
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        binding.ivMyPartnershipBackArrow.setOnClickListener {
            navigateToHome()
        }
        initAdapter()
        initScrollListener()
        initializeStampViews()
        viewModel.getUnreviewedUsage()
        stampViewModel.loadStampCount()


    }

    private fun initializeStampViews() {
        stampViews = listOf(
            binding.ivHomeStamp1, binding.ivHomeStamp2, binding.ivHomeStamp3,
            binding.ivHomeStamp4, binding.ivHomeStamp5, binding.ivHomeStamp6,
            binding.ivHomeStamp7, binding.ivHomeStamp8, binding.ivHomeStamp9,
            binding.ivHomeStamp10
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initAdapter() {
        serviceRecordAdapter = ServiceRecordAdapter()
        binding.rvHomeMyPartnershipDetailsList.apply {
            adapter = serviceRecordAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun updateStampDisplay(stampCount: Int) {
        // 이 로직은 의도에 따라 선택하세요. (10개 넘으면 2개로 보이는 로직)
        var realCount = stampCount % 10
        if (realCount == 0 && stampCount != 0) {
            realCount = 10
        }

        stampViews.forEachIndexed { index, imageView ->
            if (index < realCount) {
                // 채워진 스탬프 이미지
                imageView.setImageResource(R.drawable.ic_home_stamp_filled)
            } else {
                // 비어있는 스탬프 이미지 (ic_home_stamp가 비어있는 것이 맞는지 확인 필요)
                imageView.setImageResource(R.drawable.ic_home_stamp)
            }
        }
    }

    private fun initScrollListener() {
        binding.rvHomeMyPartnershipDetailsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition =
                    layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                // 스크롤이 마지막 아이템에 도달했고, 현재 로딩 중이 아니라면
                if (lastVisibleItemPosition == totalItemCount - 1 && !viewModel.isFetchingReviews) {
                    // 다음 페이지 로드
                    viewModel.getUnreviewedUsage()
                }
            }
        })
    }
    private fun navigateToHome() {
        findNavController().navigate(R.id.action_myPartnershipFragment_to_homeFragment)
    }
}