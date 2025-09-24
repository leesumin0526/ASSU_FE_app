package com.example.assu_fe_app.presentation.user.review.store

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.review.Review
import com.example.assu_fe_app.databinding.FragmentUserReviewStoreDetailBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.user.review.adapter.UserReviewAdapter
import com.example.assu_fe_app.presentation.user.review.mypage.OnItemClickListener
import com.example.assu_fe_app.ui.review.UserStoreGetReviewViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime

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
                getReviewViewModel.updateSort(selected.apiValue)
                getReviewViewModel.getReviews()
            }
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }

        initAdapter()
        initScrollListener()
    }

    override fun initObserver() {
        getReviewViewModel.reviewList.observe(viewLifecycleOwner) { reviews ->
            // ViewModel의 전체 리뷰 리스트를 어댑터에 제출하여 모든 리뷰를 보여줍니다.
            userReviewAdapter.submitList(reviews)
            val totalReviews = reviews.size
            binding.tvReviewStoreDetailCount.text = "작성한 리뷰가 ${totalReviews}건 있어요"
        }
    }

    private fun initAdapter(){
        userReviewAdapter = UserReviewAdapter(showDeleteButton = false, listener = null,
            showReportButton = false, reportListener = null)

        binding.fcvReviewStoreRank.apply{
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userReviewAdapter
        }
    }

    private fun initScrollListener() {
        binding.fcvReviewStoreRank.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition =
                    layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (lastVisibleItemPosition == totalItemCount - 1 && !getReviewViewModel.isFetchingReviews) {
                    getReviewViewModel.getReviews()
                }
            }
        })
    }

}