package com.ssu.assu.presentation.common.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssu.assu.databinding.ItemLocationInfoSearchBinding

class LocationInfoSearchListAdapter :
    ListAdapter<LocationInfo, LocationInfoSearchListAdapter.LocationInfoViewHolder>(LocationInfoDiffCallback()) {

    var onItemClick: ((LocationInfo) -> Unit)? = null
    inner class LocationInfoViewHolder(private val binding: ItemLocationInfoSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // 데이터를 뷰에 바인딩하는 함수
        fun bind(locationInfo: LocationInfo) {
            binding.tvLocationInfoName.text = locationInfo.name
            binding.tvLocationInfoAddress.text = locationInfo.address
            binding.ivLocationInfoCheck.visibility= View.GONE

            // 아이템 클릭 리스너 설정 등
            binding.root.setOnClickListener {
                onItemClick?.invoke(locationInfo)
            }
        }
    }

    /**
     * DiffUtil.ItemCallback: 리스트의 변경 사항을 효율적으로 계산
     */
    private class LocationInfoDiffCallback : DiffUtil.ItemCallback<LocationInfo>() {
        // 아이템의 고유 ID를 비교하여 같은 아이템인지 확인
        override fun areItemsTheSame(oldItem: LocationInfo, newItem: LocationInfo): Boolean {
            return oldItem.address == newItem.address
        }

        // 아이템의 내용(콘텐츠)이 같은지 비교
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: LocationInfo, newItem: LocationInfo): Boolean {
            return oldItem == newItem // data class의 '=='는 내용까지 비교해 줌
        }
    }

    /**
     * 새로운 ViewHolder가 필요할 때 호출
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationInfoViewHolder {
        val binding =
            ItemLocationInfoSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationInfoViewHolder(binding)
    }

    /**
     * ViewHolder에 데이터를 표시할 때 호출
     */
    override fun onBindViewHolder(holder: LocationInfoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}