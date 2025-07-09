package com.example.assu_fe_app.presentation.user.review.adapter

import android.os.Build
import android.view.View
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.review.Review
import com.example.assu_fe_app.databinding.ItemReviewBinding
import com.example.assu_fe_app.presentation.user.review.mypage.OnItemClickListener
import java.time.format.DateTimeFormatter

class UserReviewViewHolder(
    private val binding: ItemReviewBinding,
    private val showDeleteButton: Boolean,
    private val listener : OnItemClickListener
) : RecyclerView.ViewHolder(binding.root){

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(review: Review) {
        binding.tvMarket.text = review.marketName
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

        binding.tvReviewDelete.visibility = if (showDeleteButton) View.VISIBLE else View.GONE
        binding.tvReviewDelete.setOnClickListener {
            listener.onClick(adapterPosition)
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

interface onItemClickListener{
    fun onClick()
}