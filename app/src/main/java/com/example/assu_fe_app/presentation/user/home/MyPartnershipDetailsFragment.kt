package com.example.assu_fe_app.presentation.user.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentMyPartnershipDetailsBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.user.home.adapter.HomeMyPartnershipDetailsReviewAdapter
import com.example.assu_fe_app.data.dto.user.home.HomeMyPartnershipDetailsReviewItem
import com.example.assu_fe_app.presentation.user.dashboard.adapter.ServiceRecordAdapter
import com.example.assu_fe_app.ui.usage.UnreviewedUsageViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MyPartnershipDetailsFragment :
    BaseFragment<FragmentMyPartnershipDetailsBinding>(R.layout.fragment_my_partnership_details) {

    private lateinit var serviceRecordAdapter: ServiceRecordAdapter
    private val viewModel : UnreviewedUsageViewModel by viewModels()

    override fun initObserver() {
        viewModel.usageList.observe(viewLifecycleOwner) { records ->
            serviceRecordAdapter.setData(records)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        binding.ivMyPartnershipBackArrow.setOnClickListener {
            navigateToHome()
        }
        initAdapter()
        initScrollListener()
        viewModel.getUnreviewedUsage()


//        val dummyList = listOf(
//            HomeMyPartnershipDetailsReviewItem("역전할머니맥주 숭실대점","역전할머니맥주에서 음료 한 병을 제공받았어요!","2025-03-15 18:57"),
//            HomeMyPartnershipDetailsReviewItem("역전할머니맥주 숭실대점","역전할머니맥주에서 음료 한 병을 제공받았어요!","2025-03-15 18:57"),
//            HomeMyPartnershipDetailsReviewItem("역전할머니맥주 숭실대점","역전할머니맥주에서 음료 한 병을 제공받았어요!","2025-03-15 18:57"),
//            HomeMyPartnershipDetailsReviewItem("역전할머니맥주 숭실대점","역전할머니맥주에서 음료 한 병을 제공받았어요!","2025-03-15 18:57"),
//            HomeMyPartnershipDetailsReviewItem("역전할머니맥주 숭실대점","역전할머니맥주에서 음료 한 병을 제공받았어요!","2025-03-15 18:57"),
//            HomeMyPartnershipDetailsReviewItem("역전할머니맥주 숭실대점","역전할머니맥주에서 음료 한 병을 제공받았어요!","2025-03-15 18:57"),
//            HomeMyPartnershipDetailsReviewItem("역전할머니맥주 숭실대점","역전할머니맥주에서 음료 한 병을 제공받았어요!","2025-03-15 18:57"),
//            HomeMyPartnershipDetailsReviewItem("역전할머니맥주 숭실대점","역전할머니맥주에서 음료 한 병을 제공받았어요!","2025-03-15 18:57"),
//            HomeMyPartnershipDetailsReviewItem("역전할머니맥주 숭실대점","역전할머니맥주에서 음료 한 병을 제공받았어요!","2025-03-15 18:57"),
//            HomeMyPartnershipDetailsReviewItem("역전할머니맥주 숭실대점","역전할머니맥주에서 음료 한 병을 제공받았어요!","2025-03-15 18:57"),
//            HomeMyPartnershipDetailsReviewItem("역전할머니맥주 숭실대점","역전할머니맥주에서 음료 한 병을 제공받았어요!","2025-03-15 18:57"),
//            HomeMyPartnershipDetailsReviewItem("역전할머니맥주 숭실대점","역전할머니맥주에서 음료 한 병을 제공받았어요!","2025-03-15 18:57")
//        )
//        adapter = HomeMyPartnershipDetailsReviewAdapter(dummyList)
//        binding.rvHomeMyPartnershipDetailsList.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvHomeMyPartnershipDetailsList.adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initAdapter() {
        serviceRecordAdapter = ServiceRecordAdapter()
        binding.rvHomeMyPartnershipDetailsList.apply {
            adapter = serviceRecordAdapter
            layoutManager = LinearLayoutManager(requireContext())
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