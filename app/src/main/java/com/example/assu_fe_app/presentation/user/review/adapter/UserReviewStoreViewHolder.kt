package com.example.assu_fe_app.presentation.user.review.adapter

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.review.Review
import com.example.assu_fe_app.databinding.ItemReviewBinding
import java.time.format.DateTimeFormatter

class UserReviewStoreViewHolder(
    private val binding: ItemReviewBinding
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(review: Review) {
        binding.tvMarket.text = review.marketName // 그냥 marketName으로 전부 통일 시킴 이게 한 가게에 해당할때는 소속명이 표기 됨.
        binding.tvReviewContent.text = review.content

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        binding.tvReviewDate.text = "작성일 | ${review.date.format(formatter)}"

        setRating(review.rate)

        val imageViews = listOf(
            binding.ivReviewImg1,
            binding.ivReviewImg2,
            binding.ivReviewImg3
        )
        imageViews.forEach { it.visibility = View.GONE }
        review.reviewImage.take(3).forEachIndexed { index, _ ->
            imageViews[index].visibility = View.VISIBLE
        }

    }

    private fun setRating(rating: Int) {
        val starViews = listOf(
            binding.reviewStar1,
            binding.reviewStar2,
            binding.reviewStar3,
            binding.reviewStar4,
            binding.reviewStar5
        )

        for (i in starViews.indices) {
            val colorRes = if (i < rating) R.color.assu_main else R.color.assu_font_sub
            starViews[i].setColorFilter(
                ContextCompat.getColor(itemView.context, colorRes)
            )
        }
    }
}
