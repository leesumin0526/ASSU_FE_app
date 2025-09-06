package com.example.assu_fe_app.presentation.user.review.writing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.ActivityUserStarReviewBinding
import com.example.assu_fe_app.presentation.base.BaseActivity

class   UserStarReviewActivity : BaseActivity<ActivityUserStarReviewBinding>(R.layout.activity_user_star_review) {


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


        // 여기서 이전 화면에서 넘어온 데이터 쓰기

        var selectedRating = 0
        val adminName= intent.getStringExtra("adminName")
        val content : String ?= intent.getStringExtra("content")
        val partnershipUsageId : Long?= intent.getLongExtra("partnershipUsageId", 0)
        val storeId: Long?= intent.getLongExtra("storeId", 0)
        val partnerId: Long?= intent.getLongExtra("partnerId", 0)
        val storeName: String?= intent.getStringExtra("storeName")

        binding.tvStarReviewPartnership.text = adminName
        binding.tvStarReviewPlaceName.text= storeName
        // 별 정의
        val stars = listOf(
            binding.ivStarReviewStar1,
            binding.ivStarReviewStar2,
            binding.ivStarReviewStar3,
            binding.ivStarReviewStar4,
            binding.ivStarReviewStar5
        )


        fun setStars(rating: Int) {
            for (i in stars.indices) {
                val drawableRes = if (i < rating) R.drawable.ic_activated_star
                else R.drawable.ic_deactivated_star
                stars[i].setImageResource(drawableRes)
            }
        }

        val deactivatedButton = binding.layoutWriteReviewDeactivatedButton
        val activatedButton = binding.layoutWriteReviewActivatedButton

        for ((index, star) in stars.withIndex()) {
            star.setOnClickListener {
                selectedRating = index+1
                setStars(selectedRating)
                deactivatedButton.visibility= View.GONE
                activatedButton.visibility=View.VISIBLE
            }
        }


        // 뒤로 가기 버튼 (activity -> fragment 전환. 그저 백스텝이어서 finish로 activity 끝냄)
        val backButton = binding.ivStarReviewBackArrow
        backButton.setOnClickListener {
            finish() // 액티비티 종료 → 이전 화면(프래그먼트 혹은 액티비티)로 돌아감
        }



        val writeReviewButton = binding.layoutWriteReviewActivatedButton
        writeReviewButton.setOnClickListener {
            val intent = Intent(this, UserPhotoReviewActivity::class.java).apply {
                putExtra("rating", selectedRating)
                putExtra("adminName",adminName )
                putExtra("content", content)
                putExtra("partnershipUsageId", partnershipUsageId)
                putExtra("storeId", storeId)
                putExtra("partnerId", partnerId)
                putExtra("storeName", storeName)
            }
            startActivity(intent)
        }
    }

    override fun initObserver() {
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        val marketName = intent.getStringExtra("marketName")
        binding.tvStarReviewPlaceName.text = marketName
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}