package com.example.assu_fe_app.presentation.common.contract.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.partner_admin.home.PartnershipContractItem
import com.example.assu_fe_app.databinding.ItemPartnershipContentListBinding


class PartnershipContractAdapter (
    private val items: List<PartnershipContractItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemPartnershipContentListBinding)
        :RecyclerView.ViewHolder(binding.root) {
            fun bind(item: PartnershipContractItem) {
                when (item) {
                    is PartnershipContractItem.Service.ByPeople -> {
                        binding.tvType.text = "서비스 제공"
                        binding.tvPartnershipConditionPeople.text = item.minPeople.toString()
                        binding.tvPartnershipConditionDetails1.text = "인 이상일 경우"
                        binding.tvPartnershipConditionGoods.text = item.items
                        binding.tvPartnershipConditionDetails2.text = "제공"
                    }
                    is PartnershipContractItem.Service.ByAmount ->{
                        binding.tvType.text = "서비스 제공"
                        binding.tvPartnershipConditionPeople.text = item.minAmount.toString()
                        binding.tvPartnershipConditionDetails1.text = "원 이상일 경우"
                        binding.tvPartnershipConditionGoods.text = item.items
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
        val binding = ItemPartnershipContentListBinding.inflate(
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