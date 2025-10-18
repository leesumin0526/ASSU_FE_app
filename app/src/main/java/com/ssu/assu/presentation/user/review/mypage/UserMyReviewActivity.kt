package com.ssu.assu.presentation.user.review.mypage

import android.content.Context
import android.os.Build
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssu.assu.R
import com.ssu.assu.databinding.ActivityUserMyReviewBinding
import com.ssu.assu.presentation.base.BaseActivity
import com.ssu.assu.presentation.common.report.OnItemClickListener
import com.ssu.assu.presentation.user.review.adapter.UserReviewAdapter
import com.ssu.assu.presentation.user.review.store.ReviewSortBottomSheet
import com.ssu.assu.presentation.user.review.store.SortType
import com.ssu.assu.ui.review.GetReviewViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserMyReviewActivity :
    BaseActivity<ActivityUserMyReviewBinding>(R.layout.activity_user_my_review),
    OnItemClickListener, OnReviewDeleteConfirmedListener {

    private val getReviewViewModel: GetReviewViewModel by viewModels()
    private lateinit var userReviewAdapter: UserReviewAdapter
    private val manager = supportFragmentManager
    private var currentSort: SortType = SortType.LATEST

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val extraPaddingTop = 3
            v.setPadding(
                systemBars.left,
                systemBars.top + extraPaddingTop.dpToPx(v.context),
                systemBars.right,
                0
            )
            insets
        }

        binding.btnManageReviewBack.setOnClickListener {
            finish()
        }

        // 정렬 버튼 클릭 이벤트 추가
        binding.llMySort.setOnClickListener {
            val bottomSheet = ReviewSortBottomSheet { selected ->
                currentSort = selected
                // 정렬 텍스트 업데이트 (필요한 경우)
                 binding.tvPreorder.text = selected.label

                // ViewModel의 updateSort만 호출 (내부에서 getReviews 호출됨)
                getReviewViewModel.updateSort(selected.apiValue)

                // 리스트를 맨 위로 스크롤
                binding.rvManageReview.scrollToPosition(0)
            }
            bottomSheet.show(manager, bottomSheet.tag)
        }

        initAdapter()
        initScrollListener()
    }

    override fun initObserver() {
        getReviewViewModel.reviewList.observe(this) { reviews ->
            if (reviews.isNullOrEmpty()) {
                // 빈 리스트 처리
                userReviewAdapter.submitList(emptyList())
                binding.tvManageReviewReviewCount.text = "0"

                binding.flManageReviewReviewNull.visibility = View.VISIBLE
                binding.flManageReviewReviewExist.visibility = View.GONE
            } else {
                userReviewAdapter.submitList(reviews)
                binding.tvManageReviewReviewCount.text = reviews.size.toString()
                binding.flManageReviewReviewNull.visibility = View.GONE
                binding.flManageReviewReviewExist.visibility = View.VISIBLE
            }
        }
    }

    fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initAdapter() {
        userReviewAdapter = UserReviewAdapter(
            showDeleteButton = true,
            listener = this,
            showReportButton = false,
            reportListener = null
        )

        binding.rvManageReview.apply {
            layoutManager = LinearLayoutManager(this@UserMyReviewActivity)
            adapter = userReviewAdapter
        }

        binding.tvManageReviewReviewCount.text = userReviewAdapter.itemCount.toString()
    }

    private fun initScrollListener() {
        binding.rvManageReview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                // 마지막 아이템이 보이고, 현재 로딩 중이 아니며, 마지막 페이지가 아닐 때만 다음 페이지 로드
                if (lastVisibleItemPosition == totalItemCount - 1 &&
                    !getReviewViewModel.isFetchingReviews &&
                    !getReviewViewModel.isLastPage) {
                    getReviewViewModel.getReviews()
                }
            }
        })
    }

    override fun onClick(position: Int) {
        val dialog = ReviewDeleteDialogFragment.newInstance(position)
        dialog.show(manager, "ReviewDeleteDialogFragment")
    }

    override fun onReviewDeleteConfirmed(position: Int) {
        // 해당 position의 리뷰 ID 가져오기
        val currentList = userReviewAdapter.currentList
        if (position < currentList.size) {
            val reviewToDelete = currentList[position]
            // ViewModel을 통해 서버에서 삭제
            getReviewViewModel.deleteReview(reviewToDelete.id)
        }
    }
}