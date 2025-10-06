package com.ssu.assu.presentation.admin.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssu.assu.R
import com.ssu.assu.databinding.ItemAdminPendingContractBinding
import com.ssu.assu.domain.model.partnership.SuspendedPaperModel

class AdminPendingContractAdapter(
    private val onDeleteClick: (SuspendedPaperModel) -> Unit,
    private val onItemClick: (SuspendedPaperModel) -> Unit
) : ListAdapter<SuspendedPaperModel, AdminPendingContractAdapter.ViewHolder>(Diff()) {

    private var selectedPosition: Int? = null

    inner class ViewHolder(
        private val binding: ItemAdminPendingContractBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SuspendedPaperModel, isSelected: Boolean) {
            binding.tvStoreName.text = item.partnerName
            binding.tvProposalDate.text = item.createdAt.toString().split("T")[0]

            binding.root.setBackgroundResource(
                if (isSelected) R.color.assu_sub3 else android.R.color.transparent
            )

            binding.btnDelete.setOnClickListener { onDeleteClick(item) }
            binding.root.setOnClickListener { onItemClick(item) } // 배경 변경은 Fragment에서 select 호출
        }
    }


    override fun onCreateViewHolder(p: ViewGroup, v: Int) =
        ViewHolder(ItemAdminPendingContractBinding.inflate(LayoutInflater.from(p.context), p, false))

    override fun onBindViewHolder(h: ViewHolder, pos: Int) =
        h.bind(getItem(pos), pos == selectedPosition)

    /** 선택 표시 */
    fun selectById(paperId: Long) {
        val idx = currentList.indexOfFirst { it.paperId == paperId }
        if (idx == -1) return
        val prev = selectedPosition
        selectedPosition = idx
        prev?.let { notifyItemChanged(it) }
        notifyItemChanged(idx)
    }

    /** 선택 해제 */
    fun clearSelection() {
        val prev = selectedPosition ?: return
        selectedPosition = null
        notifyItemChanged(prev)
    }

    private class Diff : DiffUtil.ItemCallback<SuspendedPaperModel>() {
        override fun areItemsTheSame(o: SuspendedPaperModel, n: SuspendedPaperModel) = o.paperId == n.paperId
        override fun areContentsTheSame(o: SuspendedPaperModel, n: SuspendedPaperModel) = o == n
    }
}