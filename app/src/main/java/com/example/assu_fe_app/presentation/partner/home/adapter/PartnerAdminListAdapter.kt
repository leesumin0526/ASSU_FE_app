package com.example.assu_fe_app.presentation.partner.home.adapter

import com.example.assu_fe_app.databinding.ItemAssociationListBinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.presentation.common.contract.PartnershipContentDialogFragment
import com.example.assu_fe_app.data.dto.partner_admin.home.PartnershipContractItem

// 데이터 모델 정의
data class PartnerAdminListItem(
    val adminName: String,
    val benefitDescription: String,
    val benefitPeriod: String
)

class PartnerAdminListAdapter(
    private val items: List<PartnerAdminListItem>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<PartnerAdminListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAssociationListBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PartnerAdminListItem) {
            binding.tvAssociationName.text = item.adminName
            binding.tvBenefitDescription.text = item.benefitDescription
            binding.tvBenefitPeriod.text = item.benefitPeriod

            itemView.setOnClickListener {
                val dialog = PartnershipContentDialogFragment(dummyItem)
                dialog.show(fragmentManager, "PartnershipContentFragment")
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