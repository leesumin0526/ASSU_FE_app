package com.example.assu_fe_app.presentation.user.review.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.data.dto.review.Review
import com.example.assu_fe_app.databinding.ItemReviewBinding
import com.example.assu_fe_app.presentation.user.review.mypage.OnItemClickListener

class UserReviewAdapter(
    private val showDeleteButton: Boolean = false,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<UserReviewViewHolder>(){

    private val reviewList = mutableListOf<Review>()

    fun setData(newList: List<Review>) {
        reviewList.clear()
        reviewList.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        if (position in reviewList.indices) {
            reviewList.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserReviewViewHolder(binding, showDeleteButton, listener)
    }

    override fun getItemCount() = reviewList.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: UserReviewViewHolder, position: Int) {
        holder.bind(reviewList[position])
    }
}