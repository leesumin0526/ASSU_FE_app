package com.example.assu_fe_app.presentation.user.review.adapter

import android.os.Build
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.review.Review
import com.example.assu_fe_app.databinding.ItemReviewBinding
import com.example.assu_fe_app.presentation.user.review.mypage.OnItemClickListener
import java.time.format.DateTimeFormatter

class UserReviewViewHolder(
    private val binding: ItemReviewBinding,
    private val showDeleteButton: Boolean,
    private val listener : OnItemClickListener?,
    private val showReportButton: Boolean,
    private val reportListener: OnItemClickListener?
) : RecyclerView.ViewHolder(binding.root){

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(review: Review) {
        binding.tvMarket.text = review.marketName
        binding.tvReviewContent.text = review.content

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        binding.tvReviewDate.text = "작성일 | ${review.date.format(formatter)}"

        setRating(review.rate)
        loadReviewImages(review.reviewImage)
        setupActionButton()
    }

    private fun setupActionButton() {
        when {
            // 신고 버튼을 보여줘야 하는 경우
            showReportButton -> {
                binding.tvReviewDelete.visibility = View.VISIBLE
                binding.tvReviewDelete.text = "신고하기"
                binding.tvReviewDelete.setOnClickListener {
                    Log.d("reviewAdapter : ", "신고하기가 클릭 되었어요.")
                    reportListener?.onClick(adapterPosition)
                }
            }
            // 삭제 버튼을 보여줘야 하는 경우
            showDeleteButton -> {
                binding.tvReviewDelete.visibility = View.VISIBLE
                binding.tvReviewDelete.text = "삭제하기" // 원래 텍스트
                binding.tvReviewDelete.setOnClickListener {
                    listener?.onClick(adapterPosition)
                }
            }
            // 둘 다 보여주지 않는 경우
            else -> {
                binding.tvReviewDelete.visibility = View.GONE
            }
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

    private fun loadReviewImages(imageUrls: List<String>) {
        val imageViews = listOf(
            binding.ivReviewImg1,
            binding.ivReviewImg2,
            binding.ivReviewImg3
        )

        // 모든 이미지뷰 초기화
        imageViews.forEach { imageView ->
            imageView.visibility = View.GONE
            // 기존 이미지 클리어
            Glide.with(imageView.context).clear(imageView)
        }

        // 이미지 URL이 있는 만큼만 표시 (최대 3개)
        imageUrls.take(3).forEachIndexed { index, imageUrl ->
            val imageView = imageViews[index]
            imageView.visibility = View.VISIBLE

            // Glide로 이미지 로딩
            Glide.with(imageView.context)
                .load(imageUrl)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.bg_review_image_box) // 로딩 중 표시
                        .error(R.drawable.bg_review_image_box) // 로딩 실패 시 표시
                        .centerCrop() // 이미지를 뷰에 맞게 크롭
                        .transform(RoundedCorners(12)) // 모서리 둥글게 (선택사항)
                )
                .into(imageView)
        }
    }
}
