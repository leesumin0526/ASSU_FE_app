package com.example.assu_fe_app.presentation.admin.dashboard.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.databinding.ItemSuggestionListBinding
import com.example.assu_fe_app.domain.model.suggestion.SuggestionModel
import com.example.assu_fe_app.util.toDepartmentName
import com.example.assu_fe_app.util.toEnrollmentStatus

data class AdminSuggestionItem(
    val departmentInfo: String,
    val status: String,
    val content: String,
    val date: String
)

class AdminSuggestionListAdapter : RecyclerView.Adapter<AdminSuggestionListAdapter.ViewHolder>() {

    private var items: List<SuggestionModel> = emptyList()

    inner class ViewHolder(private val binding: ItemSuggestionListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SuggestionModel) {
            binding.tvStoreName.text = item.storeName
            binding.tvDepartment.text = item.departmentInfo.toDepartmentName()
            binding.tvStatus.text = item.status.toEnrollmentStatus()
            binding.tvContent.text = item.content
            binding.tvDate.text = "작성일 | ${item.date}"
        }
    }

    fun submitList(newItems: List<SuggestionModel>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSuggestionListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}