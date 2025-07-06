package com.example.assu_fe_app.presentation.user.review.mypage

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.review.Review
import com.example.assu_fe_app.databinding.ActivityUserMyReviewBinding
import com.example.assu_fe_app.presentation.base.BaseActivity
import com.example.assu_fe_app.presentation.user.review.adapter.UserReviewAdapter
import java.time.LocalDateTime

class UserMyReviewActivity : BaseActivity<ActivityUserMyReviewBinding>(R.layout.activity_user_my_review)
    , OnItemClickListener, OnReviewDeleteConfirmedListener {

    private lateinit var userReviewAdapter : UserReviewAdapter
    val manager = supportFragmentManager


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
    }

    override fun initObserver() {

    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initAdapter(){
        //adapter초기화
        userReviewAdapter = UserReviewAdapter(showDeleteButton = true, listener = this )

        binding.rvManageReview.apply {
            layoutManager = LinearLayoutManager(this@UserMyReviewActivity)
            adapter = userReviewAdapter
        }

        // 여기에 review List가 null 일때 ui 업데이트 관련 사항도 해줘야 함.

        userReviewAdapter.setData(createDummyData())
        binding.tvManageReviewReviewCount.text = userReviewAdapter.itemCount.toString()

    }

    override fun onClick(position : Int) {
        val dialog = ReviewDeleteDialogFragment.newInstance(position)
        dialog.show(manager, "ReviewDeleteDialogFragment")
    }

    override fun onReviewDeleteConfirmed(position: Int) {
        userReviewAdapter.removeAt(position)
        binding.tvManageReviewReviewCount.text = userReviewAdapter.itemCount.toString()
    }

    // 임의의 DummyData를 생성하는 함수
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createDummyData(): List<Review>{
        return listOf(
            Review(
                marketName = "스시천국",
                studentCategory = "경영대학 재학생",
                rate = 5,
                content = "진짜 맛있었어요! 또 가고 싶어요!",
                date = LocalDateTime.now().minusDays(1),
                reviewImage = listOf()
            ),
            Review(
                marketName = "돈까스집",
                studentCategory = "경영대학 재학생",
                rate = 4,
                content = "튀김이 바삭해서 좋았어요. 양도 많아요.",
                date = LocalDateTime.now().minusDays(3),
                reviewImage = listOf()
            )
        )
    }
}