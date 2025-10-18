package com.ssu.assu.presentation.user.review.store

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssu.assu.R
import com.ssu.assu.databinding.FragmentUserReviewStoreDetailBinding
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.presentation.user.review.adapter.UserReviewAdapter
import com.ssu.assu.ui.review.UserStoreGetReviewViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserReviewStoreDetailFragment :
    BaseFragment<FragmentUserReviewStoreDetailBinding>(R.layout.fragment_user_review_store_detail) {

    private lateinit var userReviewAdapter: UserReviewAdapter
    private val getReviewViewModel: UserStoreGetReviewViewModel by activityViewModels()
    private var currentSort: SortType = SortType.LATEST

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        binding.tvReviewStoreName.text = getReviewViewModel.storeName

        binding.ivReviewStoreBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.llSort.setOnClickListener {
            val bottomSheet = ReviewSortBottomSheet { selected ->
                currentSort = selected
                binding.tvReviewStoreReviewAll.text = selected.label

                // ViewModel의 updateSort만 호출 (내부에서 getReviews 호출됨)
                getReviewViewModel.updateSort(selected.apiValue)

                // 리스트를 맨 위로 스크롤
                binding.fcvReviewStoreRank.scrollToPosition(0)
            }
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }

        initAdapter()
        initScrollListener()
    }

    override fun initObserver() {
        getReviewViewModel.reviewList.observe(viewLifecycleOwner) { reviews ->
            userReviewAdapter.submitList(reviews)
            val totalReviews = reviews.size
            binding.tvReviewStoreDetailCount.text = "작성한 리뷰가 ${totalReviews}건 있어요"
        }
    }

    private fun initAdapter(){
        userReviewAdapter = UserReviewAdapter(
            showDeleteButton = false,
            listener = null,
            showReportButton = false,
            reportListener = null
        )

        binding.fcvReviewStoreRank.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userReviewAdapter
        }
    }

    private fun initScrollListener() {
        binding.fcvReviewStoreRank.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                // 마지막 아이템이 보이고, 현재 로딩 중이 아닐 때만 다음 페이지 로드
                if (lastVisibleItemPosition == totalItemCount - 1 &&
                    !getReviewViewModel.isFetchingReviews &&
                    !getReviewViewModel.isLastPage) {
                    getReviewViewModel.getReviews()
                }
            }
        })
    }
}