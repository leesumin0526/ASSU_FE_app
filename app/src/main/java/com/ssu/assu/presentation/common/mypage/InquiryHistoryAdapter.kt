package com.ssu.assu.presentation.common.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssu.assu.R
import com.ssu.assu.databinding.ItemInquiryHistoryBinding
import com.ssu.assu.domain.model.inquiry.InquiryModel

class InquiryHistoryAdapter(
    private val onClick: (InquiryModel) -> Unit
) : ListAdapter<InquiryModel, InquiryHistoryAdapter.VH>(diff) {

    companion object {
        private val diff = object : DiffUtil.ItemCallback<InquiryModel>() {
            override fun areItemsTheSame(a: InquiryModel, b: InquiryModel) = a.id == b.id
            override fun areContentsTheSame(a: InquiryModel, b: InquiryModel) = a == b
        }
    }

    inner class VH(private val b: ItemInquiryHistoryBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(m: InquiryModel) = with(b) {
            tvInquiryTitle.text = m.title

            // ----- Status 변환 -----
            when (m.status) {
                "WAITING" -> {
                    tvInquiryStatus.text = "답변 대기중"
                    tvInquiryStatus.setTextColor(
                        root.context.getColor(R.color.assu_font_sub)
                    )
                }
                "ANSWERED" -> {
                    tvInquiryStatus.text = "답변 완료"
                    tvInquiryStatus.setTextColor(
                        root.context.getColor(R.color.assu_main)
                    )
                }
                else -> {
                    tvInquiryStatus.text = m.status
                    tvInquiryStatus.setTextColor(
                        root.context.getColor(R.color.assu_font_sub)
                    )
                }
            }

            // ----- 날짜/시간 분리 -----
            m.createdAt?.let { iso ->
                // ex) "2025-09-13T13:52:01.606942"
                try {
                    val parts = iso.split("T")
                    if (parts.size == 2) {
                        val datePart = parts[0]               // "2025-09-13"
                        val timePart = parts[1].take(5)       // "13:52"
                        tvInquiryDate.text = "$datePart"
                        tvInquiryTime.text = "$timePart"
                    } else {
                        tvInquiryDate.text = iso
                        tvInquiryTime.text = ""
                    }
                } catch (e: Exception) {
                    tvInquiryDate.text = iso
                    tvInquiryTime.text = ""
                }
            } ?: run {
                tvInquiryDate.text = ""
                tvInquiryTime.text = ""
            }

            // 클릭
            root.setOnClickListener { onClick(m) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemInquiryHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))
}
