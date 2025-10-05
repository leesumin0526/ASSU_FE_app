package com.assu.app.presentation.user.review.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.assu.app.data.dto.review.ReviewStoreItem
import com.assu.app.databinding.ItemReviewStoreBinding


class UserReviewStoreAdapter :
    ListAdapter<ReviewStoreItem, UserReviewStoreAdapter.ViewHolder>(DiffCallback) {


    inner class ViewHolder(private val binding: ItemReviewStoreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReviewStoreItem) {
            binding.tvReviewStoreItemOrganization.text = item.organization
            binding.tvReviewStoreItemContent.text = item.content
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReviewStoreBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // items[position] 대신 getItem(position)을 사용합니다.
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ReviewStoreItem>() {
            // 두 아이템이 동일한 항목인지 확인합니다. (보통 고유 ID로 비교)
            override fun areItemsTheSame(oldItem: ReviewStoreItem, newItem: ReviewStoreItem): Boolean {
                return oldItem.organization == newItem.organization && oldItem.content == newItem.content
            }

            override fun areContentsTheSame(oldItem: ReviewStoreItem, newItem: ReviewStoreItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}