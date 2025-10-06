package com.ssu.assu.presentation.partner.dashboard.review

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.ssu.assu.data.dto.review.Review
import com.ssu.assu.databinding.ItemReviewBinding
import com.ssu.assu.presentation.user.review.adapter.UserReviewStoreViewHolder

class PartnerReviewAdapter() : RecyclerView.Adapter<UserReviewStoreViewHolder>() {

    private val reviewList = mutableListOf<Review>()

    fun setData(newList: List<Review>) {
        reviewList.clear()
        reviewList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserReviewStoreViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserReviewStoreViewHolder(binding)
    }

    override fun getItemCount() = reviewList.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: UserReviewStoreViewHolder, position: Int) {
        holder.bind(reviewList[position])
    }
}