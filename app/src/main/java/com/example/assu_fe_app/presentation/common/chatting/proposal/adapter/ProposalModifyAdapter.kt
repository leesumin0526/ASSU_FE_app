package com.example.assu_fe_app.presentation.common.chatting.proposal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.data.dto.partnership.BenefitItem
import com.example.assu_fe_app.data.dto.partnership.CriterionType
import com.example.assu_fe_app.data.dto.partnership.OptionType
import com.example.assu_fe_app.databinding.ItemPartnershipContentModifyListBinding

class ProposalModifyAdapter(
    private val onItemClick: (Int, ProposalModifyItem) -> Unit
) : ListAdapter<ProposalModifyItem, ProposalModifyAdapter.ViewHolder>(ProposalModifyDiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPartnershipContentModifyListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemPartnershipContentModifyListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProposalModifyItem) {
            // TODO: 실제 아이템 레이아웃에 맞게 바인딩
            binding.root.setOnClickListener {
                onItemClick(bindingAdapterPosition, item)
            }
        }
    }
}

object ProposalModifyDiffCallback : DiffUtil.ItemCallback<ProposalModifyItem>() {
    override fun areItemsTheSame(oldItem: ProposalModifyItem, newItem: ProposalModifyItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProposalModifyItem, newItem: ProposalModifyItem): Boolean {
        return oldItem == newItem
    }
}

data class ProposalModifyItem(
    val id: String,
    val optionType: String, // "서비스 제공" 또는 "할인 혜택"
    val criterionType: String, // "금액" 또는 "인원"
    val criterionValue: String, // 기준값
    val content: String, // 표시할 내용 (제공 항목들 또는 할인율)
    val category: String = "", // 카테고리 (서비스 제공일 때만)
)

fun BenefitItem.toProposalModifyItem(): ProposalModifyItem {
    val optionTypeText = when (this.optionType) {
        OptionType.SERVICE -> "서비스 제공"
        OptionType.DISCOUNT -> "할인 혜택"
    }

    val criterionTypeText = when (this.criterionType) {
        CriterionType.PRICE -> "금액"
        CriterionType.HEADCOUNT -> "인원"
    }

    val criterionValueText = when (this.criterionType) {
        CriterionType.PRICE -> "${this.criterionValue}원 이상"
        CriterionType.HEADCOUNT -> "${this.criterionValue}명 이상"
    }

    val contentText = when (this.optionType) {
        OptionType.SERVICE -> {
            val goodsText = this.goods.filter { it.isNotBlank() }.joinToString(", ")
            if (goodsText.isNotBlank()) goodsText else "제공 항목 없음"
        }
        OptionType.DISCOUNT -> {
            if (this.discountRate.isNotBlank()) "${this.discountRate}% 할인" else "할인율 미설정"
        }
    }

    return ProposalModifyItem(
        id = this.id,
        optionType = optionTypeText,
        criterionType = criterionTypeText,
        criterionValue = criterionValueText,
        content = contentText,
        category = this.category,
    )
}