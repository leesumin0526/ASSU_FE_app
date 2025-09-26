package com.example.assu_fe_app.presentation.admin.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.databinding.ItemSuggestionListBinding
import com.example.assu_fe_app.domain.model.suggestion.SuggestionModel
import com.example.assu_fe_app.presentation.common.report.OnItemClickListener
import com.example.assu_fe_app.util.toDepartmentName
import com.example.assu_fe_app.util.toEnrollmentStatus

class AdminSuggestionListAdapter(
    private val reportListener: OnItemClickListener?
) : ListAdapter<SuggestionModel, AdminSuggestionListAdapter.ViewHolder>(AdminSuggestionDiffCallback()) {

    inner class ViewHolder(private val binding: ItemSuggestionListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SuggestionModel) {
            binding.tvStoreName.text = item.storeName
            binding.tvDepartment.text = item.departmentInfo.toDepartmentName()
            binding.tvStatus.text = item.status.toEnrollmentStatus()
            binding.tvContent.text = item.content
            binding.tvDate.text = "작성일 | ${item.date}"


            // 신고 버튼 클릭 리스너 설정 (tv_report_suggestion이 있다고 가정)
            binding.tvReportSuggestion.setOnClickListener {
                reportListener?.onClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSuggestionListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 안전한 접근을 위한 범위 체크
        if (position < itemCount && position >= 0) {
            holder.bind(getItem(position))
        }
    }
}

class AdminSuggestionDiffCallback : DiffUtil.ItemCallback<SuggestionModel>() {
    // 두 아이템이 동일한 아이템인지 확인 (보통 고유 ID로 비교)
    override fun areItemsTheSame(oldItem: SuggestionModel, newItem: SuggestionModel): Boolean {
        return oldItem.suggestionId == newItem.suggestionId // SuggestionModel에 id 필드가 있다고 가정
    }

    // 두 아이템의 내용이 동일한지 확인
    override fun areContentsTheSame(oldItem: SuggestionModel, newItem: SuggestionModel): Boolean {
        return oldItem == newItem
    }
}