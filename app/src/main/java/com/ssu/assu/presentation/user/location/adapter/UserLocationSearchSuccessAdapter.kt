package com.ssu.assu.presentation.user.location.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssu.assu.R
import com.ssu.assu.data.dto.location.LocationUserSearchResultItem
import com.ssu.assu.databinding.ItemUserLocationSearchResultItemBinding
import com.ssu.assu.presentation.user.review.store.UserReviewStoreActivity

// 1. ListAdapter 상속으로 변경
class UserLocationSearchSuccessAdapter :
    ListAdapter<LocationUserSearchResultItem, UserLocationSearchSuccessAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemUserLocationSearchResultItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LocationUserSearchResultItem, isLastItem: Boolean) = with(binding) {
            tvLocationSearchResultItemShopName.text = item.shopName
            tvLocationSearchResultItemOrganization.text = item.organization
            tvLocationItemContent.text = item.content

            // ── 프로필 이미지 로딩 (+ 기본이미지 폴백)
            val fallbackRes = R.drawable.img_partner
            val url = item.profileUrl
            if (url.isNullOrBlank() || url.endsWith(".svg", ignoreCase = true)) {
                ivLocationSearchResultItem.setImageResource(fallbackRes)
            } else {
                Glide.with(root.context)
                    .load(url)
                    .placeholder(fallbackRes)
                    .error(fallbackRes)
                    .into(ivLocationSearchResultItem)
            }

            viewLocationSearchResultItemLine.visibility =
                if (isLastItem) View.GONE else View.VISIBLE

            root.setOnClickListener {
                val context = it.context
                val intent = Intent(context, UserReviewStoreActivity::class.java).apply {
                    putExtra("storeId", item.storeId)
                    putExtra("storeName", item.shopName)
                    putExtra("profileUrl", item.profileUrl) // (선택) 필요 시 전달
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserLocationSearchResultItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        val isLast = position == itemCount - 1
        holder.bind(currentItem, isLast)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<LocationUserSearchResultItem>() {
            override fun areItemsTheSame(
                oldItem: LocationUserSearchResultItem,
                newItem: LocationUserSearchResultItem
            ) = oldItem.storeId == newItem.storeId

            override fun areContentsTheSame(
                oldItem: LocationUserSearchResultItem,
                newItem: LocationUserSearchResultItem
            ) = oldItem == newItem
        }
    }
}