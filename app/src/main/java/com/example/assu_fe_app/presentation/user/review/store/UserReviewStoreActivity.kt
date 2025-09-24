package com.example.assu_fe_app.presentation.user.review.store

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.review.Review
import com.example.assu_fe_app.data.dto.review.ReviewStoreItem
import com.example.assu_fe_app.databinding.ActivityUserReviewStoreBinding
import com.example.assu_fe_app.presentation.base.BaseActivity
import com.example.assu_fe_app.presentation.user.review.adapter.UserReviewAdapter
import com.example.assu_fe_app.presentation.user.review.adapter.UserReviewStoreAdapter
import com.example.assu_fe_app.presentation.user.review.mypage.OnItemClickListener
import com.example.assu_fe_app.ui.review.UserStoreGetReviewViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Long.getLong
import java.time.LocalDateTime

@AndroidEntryPoint
class UserReviewStoreActivity :
    BaseActivity<ActivityUserReviewStoreBinding>(R.layout.activity_user_review_store) {

    private lateinit var userReviewAdapter: UserReviewAdapter
    private lateinit var userPartnershipAdapter : UserReviewStoreAdapter
    private val getStoreReviewViewModel: UserStoreGetReviewViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        // 시스템 바 여백 적용
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

        initStoreReviewAdapter()

        val storeName : String? = intent.getStringExtra("storeName")
        val storeId : Long = intent.getLongExtra("storeId", 0L)

        binding.tvReviewStoreName.text = storeName
        getStoreReviewViewModel.storeName = storeName.toString()
        getStoreReviewViewModel.initStoreId(storeId)
        getStoreReviewViewModel.getAverage()

        getStoreReviewViewModel.getReviews()
        getStoreReviewViewModel.getPartnershipForMe()


        // 제휴 혜택 리스트
        userPartnershipAdapter = UserReviewStoreAdapter()
        binding.rcReviewStorePartnership.layoutManager = LinearLayoutManager(this)
        binding.rcReviewStorePartnership.adapter = userPartnershipAdapter



        // 전체보기 클릭 시 상세 Fragment로 전환
        binding.tvReviewStoreReviewAll.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.review_store_fragment_container, UserReviewStoreDetailFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.ivReviewStoreBack.setOnClickListener {
            finish()
        }
    }

    override fun initObserver() {
        getStoreReviewViewModel.reviewList.observe(this) { reviews ->
            // reviews 리스트의 처음 2개 항목만 가져와 어댑터에 제출합니다.
            // 리스트가 2개 미만인 경우, 있는 만큼만 가져옵니다.
            val topTwoReviews = reviews.take(2)
            userReviewAdapter.submitList(topTwoReviews)
            val count = reviews.size
            binding.tvReviewStoreReviewCount.text= "${count}개의 평가"
        }

        getStoreReviewViewModel.partnershipContentList.observe(this) { partnershipContentList ->
            userPartnershipAdapter.submitList(partnershipContentList)

            // 제휴 혜택이 없으면 타이틀 GONE
            binding.llReivewSotreContent.visibility =
                if (partnershipContentList.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        getStoreReviewViewModel.average.observe(this) { average ->
            val formatted = String.format("%.1f", average) // "3.1"
            Log.d("평점", formatted)
            binding.tvReviewStoreScore.text = formatted

            val stars = listOf(
                binding.ivReviewStoreStar1,
                binding.ivReviewStoreStar2,
                binding.ivReviewStoreStar3,
                binding.ivReviewStoreStar4,
                binding.ivReviewStoreStar5
            )


            fun setStars(rating: Int) {
                for (i in stars.indices) {
                    val drawableRes = if (i < rating) R.drawable.ic_activated_star
                    else R.drawable.ic_deactivated_star
                    stars[i].setImageResource(drawableRes)
                }
            }
            setStars(average.toInt())
        }

        getStoreReviewViewModel.partnershipContentList.observe(this)
        { partnershipContentList ->
            userPartnershipAdapter.submitList(partnershipContentList)
        }

    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    private fun initStoreReviewAdapter(){
        userReviewAdapter = UserReviewAdapter(
            showDeleteButton = false,
            listener = null,
            showReportButton = false,
            reportListener = null
        )
        binding.fcvReviewStoreRank.layoutManager = LinearLayoutManager(this)
        binding.fcvReviewStoreRank.adapter = userReviewAdapter

    }

}