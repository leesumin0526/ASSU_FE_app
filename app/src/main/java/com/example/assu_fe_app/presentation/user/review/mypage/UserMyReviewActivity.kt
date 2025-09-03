package com.example.assu_fe_app.presentation.user.review.mypage

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.review.Review
import com.example.assu_fe_app.databinding.ActivityUserMyReviewBinding
import com.example.assu_fe_app.presentation.base.BaseActivity
import com.example.assu_fe_app.presentation.user.review.adapter.UserReviewAdapter
import com.example.assu_fe_app.ui.review.GetReviewViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import kotlin.getValue

@AndroidEntryPoint
class UserMyReviewActivity :
    BaseActivity<ActivityUserMyReviewBinding>(R.layout.activity_user_my_review),
    OnItemClickListener, OnReviewDeleteConfirmedListener {

    private val getReviewViewModel: GetReviewViewModel by viewModels()

    private lateinit var userReviewAdapter: UserReviewAdapter
    private val manager = supportFragmentManager


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

        initAdapter()
        initScrollListener()
    }

    override fun initObserver() {
        getReviewViewModel.reviewList.observe(this) { reviews ->
            if (reviews.isNullOrEmpty()) {
                // 빈 리스트 처리
                userReviewAdapter.submitList(emptyList())
                binding.tvManageReviewReviewCount.text = "0"
                // 빈 상태 UI 표시 (옵션)
                // binding.emptyStateView.visibility = View.VISIBLE
            } else {
                userReviewAdapter.submitList(reviews)
                binding.tvManageReviewReviewCount.text = reviews.size.toString()
                // binding.emptyStateView.visibility = View.GONE
            }
        }
    }

    fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initAdapter() {
        //adapter초기화
        userReviewAdapter = UserReviewAdapter(showDeleteButton = true, listener = this)

        binding.rvManageReview.apply {
            layoutManager = LinearLayoutManager(this@UserMyReviewActivity)
            adapter = userReviewAdapter
        }

        // 여기에 review List가 null 일때 ui 업데이트 관련 사항도 해줘야 함.


        binding.tvManageReviewReviewCount.text = userReviewAdapter.itemCount.toString()

    }

    private fun initScrollListener() {
        binding.rvManageReview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition =
                    layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                // 스크롤이 마지막 아이템에 도달했고, 현재 로딩 중이 아니라면
                if (lastVisibleItemPosition == totalItemCount - 1 && !getReviewViewModel.isFetchingReviews) {
                    // 다음 페이지 로드
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
