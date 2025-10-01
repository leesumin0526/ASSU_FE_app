package com.example.assu_fe_app.presentation.common.chatting.proposal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.partner_admin.home.PartnershipContractItem
import com.example.assu_fe_app.databinding.ItemPartnershipContentListBinding
import com.example.assu_fe_app.databinding.ItemPartnershipContentModifyListBinding


class ProposalModifyAdapter (
    private val items: List<PartnershipContractItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemPartnershipContentModifyListBinding)
        :RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PartnershipContractItem) {
            when (item) {
                is PartnershipContractItem.Service.ByPeople -> {
                    binding.tvType.text = "서비스 제공"
                    binding.tvPartnershipConditionPeople.text = item.minPeople.toString()
                    binding.tvPartnershipConditionDetails1.text = "인 이상일 경우"
                    val displayText = formatServiceDisplay(item.category, item.items)
                    binding.tvPartnershipConditionGoods.text = displayText
                    binding.tvPartnershipConditionDetails2.text = "제공"
                }
                is PartnershipContractItem.Service.ByAmount ->{
                    binding.tvType.text = "서비스 제공"
                    binding.tvPartnershipConditionPeople.text = item.minAmount.toString()
                    binding.tvPartnershipConditionDetails1.text = "원 이상일 경우"
                    val displayText = formatServiceDisplay(item.category, item.items)
                    binding.tvPartnershipConditionGoods.text = displayText
                    binding.tvPartnershipConditionDetails2.text = "제공"
                }
                is PartnershipContractItem.Discount.ByPeople -> {
                    binding.tvType.text = "할인 혜택"
                    binding.tvPartnershipConditionPeople.text = item.minPeople.toString()
                    binding.tvPartnershipConditionDetails1.text = "인 이상일 경우"
                    binding.tvPartnershipConditionGoods.text =
                        binding.root.context.getString(R.string.discount_percent, item.percent)
                    binding.tvPartnershipConditionDetails2.text = "할인"
                }
                is PartnershipContractItem.Discount.ByAmount -> {
                    binding.tvType.text = "할인 혜택"
                    binding.tvPartnershipConditionPeople.text = item.minAmount.toString()
                    binding.tvPartnershipConditionDetails1.text = "원 이상일 경우"
                    binding.tvPartnershipConditionGoods.text =
                        binding.root.context.getString(R.string.discount_percent, item.percent)
                    binding.tvPartnershipConditionDetails2.text = "할인"
                }
            }
        }

        private fun formatServiceDisplay(category: String?, items: String): String {
            return when {
                // 카테고리가 있고 비어있지 않으면 카테고리 표시
                !category.isNullOrBlank() -> category
                // 카테고리가 없으면 items 표시 (이미 콤마로 구분된 문자열)
                else -> items
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is PartnershipContractItem.Service.ByPeople -> 0
            is PartnershipContractItem.Service.ByAmount -> 1
            is PartnershipContractItem.Discount.ByPeople -> 2
            is PartnershipContractItem.Discount.ByAmount -> 3
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemPartnershipContentModifyListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        (holder as ViewHolder).bind(item)
    }
}