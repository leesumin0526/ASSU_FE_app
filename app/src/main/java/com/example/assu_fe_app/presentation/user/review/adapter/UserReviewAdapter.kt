package com.example.assu_fe_app.presentation.user.review.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.data.dto.review.Review
import com.example.assu_fe_app.databinding.ItemReviewBinding
import com.example.assu_fe_app.presentation.user.review.mypage.OnItemClickListener

class UserReviewAdapter(
    private val showDeleteButton: Boolean = false,
    private val listener: OnItemClickListener?
) : ListAdapter<Review, UserReviewViewHolder>(UserReviewDiffCallback()) {

//    fun removeAt(position: Int) {
//        val currentList = currentList.toMutableList()
//        if (position in currentList.indices) {
//            currentList.removeAt(position)
//            submitList(currentList) // 변경된 리스트를 제출하여 UI를 업데이트
//        }
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserReviewViewHolder(binding, showDeleteButton, listener)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: UserReviewViewHolder, position: Int) {
        // 안전한 접근을 위한 범위 체크
        if (position < itemCount && position >= 0) {
            holder.bind(getItem(position))
        }
    }
}

class UserReviewDiffCallback : DiffUtil.ItemCallback<Review>() {
    // 두 아이템이 동일한 아이템인지 확인 (보통 고유 ID로 비교)
    override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem.id == newItem.id
    }

    // 두 아이템의 내용이 동일한지 확인
    override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem == newItem
    }
}