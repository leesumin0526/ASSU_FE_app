package com.example.assu_fe_app.presentation.user.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.InquiryItem
import com.example.assu_fe_app.data.dto.InquiryStatus
import com.example.assu_fe_app.databinding.ItemInquiryHistoryBinding

class InquiryHistoryAdapter(
    private val onItemClick: (InquiryItem) -> Unit
) : ListAdapter<InquiryItem, InquiryHistoryAdapter.ViewHolder>(InquiryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInquiryHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemInquiryHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(inquiry: InquiryItem) {
            binding.apply {
                tvInquiryTitle.text = inquiry.title
                tvInquiryDate.text = inquiry.date
                tvInquiryTime.text = inquiry.time

                // 상태에 따른 배경과 텍스트 색상 설정
                when (inquiry.status) {
                    InquiryStatus.PENDING -> {
                        tvInquiryStatus.text = "답변 대기중"
                        tvInquiryStatus.setTextColor(
                            itemView.context.getColor(R.color.assu_font_sub)
                        )
                        tvInquiryStatus.setBackgroundResource(R.drawable.bg_status_pending)
                    }
                    InquiryStatus.COMPLETED -> {
                        tvInquiryStatus.text = "답변 완료"
                        tvInquiryStatus.setTextColor(
                            itemView.context.getColor(R.color.assu_main)
                        )
                        tvInquiryStatus.setBackgroundResource(R.drawable.bg_status_completed)
                    }
                }
            }
        }
    }

    private class InquiryDiffCallback : DiffUtil.ItemCallback<InquiryItem>() {
        override fun areItemsTheSame(oldItem: InquiryItem, newItem: InquiryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: InquiryItem, newItem: InquiryItem): Boolean {
            return oldItem == newItem
        }
    }
}
