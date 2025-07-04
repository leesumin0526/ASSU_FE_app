package com.example.assu_fe_app.presentation.admin.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.data.dto.partner_admin.home.PartnershipContractItem
import com.example.assu_fe_app.databinding.ItemAssociationListBinding
import com.example.assu_fe_app.presentation.common.contract.PartnershipContractDialogFragment

// 데이터 모델 정의
data class AdminPartnerListItem(
    val partnerName: String,
    val benefitDescription: String,
    val benefitPeriod: String
)

class AdminPartnerListAdapter(
    private val items: List<AdminPartnerListItem>,
    private val fragmentManger: FragmentManager
) : RecyclerView.Adapter<AdminPartnerListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAssociationListBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AdminPartnerListItem) {
            binding.tvAssociationName.text = item.partnerName
            binding.tvBenefitDescription.text = item.benefitDescription
            binding.tvBenefitPeriod.text = item.benefitPeriod

            itemView.setOnClickListener {
                val dialog = PartnershipContractDialogFragment(dummyItem)
                dialog.show(fragmentManger, "PartnershipContentFragment")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAssociationListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    val dummyItem = listOf(
        PartnershipContractItem.Service.ByPeople(4,"캔음료"),
        PartnershipContractItem.Discount.ByPeople(4, 10),
        PartnershipContractItem.Service.ByAmount(10000, "캔음료"),
        PartnershipContractItem.Discount.ByAmount(10000, 10)
    )
}