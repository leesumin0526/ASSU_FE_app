package com.ssu.assu.presentation.user.location.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssu.assu.data.dto.location.LocationUserSearchResultItem
import com.ssu.assu.databinding.ItemUserLocationSearchResultItemBinding
import com.ssu.assu.presentation.user.review.store.UserReviewStoreActivity

// 1. ListAdapter 상속으로 변경
class UserLocationSearchSuccessAdapter :
    ListAdapter<LocationUserSearchResultItem, UserLocationSearchSuccessAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemUserLocationSearchResultItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LocationUserSearchResultItem, isLastItem: Boolean) {
            binding.tvLocationSearchResultItemShopName.text = item.shopName
            binding.tvLocationSearchResultItemOrganization.text = item.organization
            if (item.content == "") {
                binding.tvLocationSearchResultItemOrganization.visibility = View.GONE
                binding.tvLocationItemContent.text = item.address
            } else {
                binding.tvLocationSearchResultItemOrganization.visibility = View.VISIBLE
                binding.tvLocationItemContent.text = item.content
            }

            binding.viewLocationSearchResultItemLine.visibility =
                if (isLastItem) View.GONE else View.VISIBLE

            binding.root.setOnClickListener {
                val context = it.context
                val intent = Intent(context, UserReviewStoreActivity::class.java)
                intent.putExtra("storeId", item.storeId)
                intent.putExtra("storeName", item.shopName)
                context.startActivity(intent)
            }
        }
    }

    // onCreateViewHolder 코드는 동일합니다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserLocationSearchResultItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    // 4. onBindViewHolder 수정
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // getItem(position)을 사용해 현재 아이템을 가져옵니다.
        val currentItem = getItem(position)
        // itemCount는 ListAdapter가 관리하는 전체 아이템 개수입니다.
        val isLast = position == itemCount - 1
        holder.bind(currentItem, isLast)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<LocationUserSearchResultItem>() {
            // 두 아이템이 동일한 항목을 나타내는지 확인 (보통 고유 ID로 비교)
            override fun areItemsTheSame(
                oldItem: LocationUserSearchResultItem,
                newItem: LocationUserSearchResultItem
            ): Boolean {
                return oldItem.storeId == newItem.storeId
            }

            // 두 아이템의 데이터 내용이 같은지 확인
            override fun areContentsTheSame(
                oldItem: LocationUserSearchResultItem,
                newItem: LocationUserSearchResultItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}