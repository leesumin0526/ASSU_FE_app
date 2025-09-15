package com.example.assu_fe_app.presentation.admin.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.data.dto.partner_admin.home.PartnershipContractItem
import com.example.assu_fe_app.data.dto.partnership.PartnershipContractData
import com.example.assu_fe_app.databinding.ItemAssociationListBinding
import com.example.assu_fe_app.domain.model.admin.GetProposalPartnerListModel
import com.example.assu_fe_app.presentation.common.contract.PartnershipContractDialogFragment


class AdminPartnerListAdapter(
    private val items: List<GetProposalPartnerListModel>,
    private val fragmentManger: FragmentManager,
    private val proposerName: String // TokenManager에서 불러온 관리자 이름
) : RecyclerView.Adapter<AdminPartnerListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAssociationListBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GetProposalPartnerListModel) {
            binding.tvAssociationName.text = item.partnerId.toString()
            binding.tvBenefitDescription.text =
                item.options.firstOrNull()?.optionType?.name ?: "제휴 혜택 없음"
            binding.tvBenefitPeriod.text =
                "${item.partnershipPeriodStart} ~ ${item.partnershipPeriodEnd}"

            itemView.setOnClickListener {
                val contractData = PartnershipContractData(
                    partnerName = item.partnerId.toString(),
                    adminName = item.adminId.toString(),
                    options = item.options.map { opt ->
                        // 이미 PartnershipContractItem으로 변환된 구조라면 그대로 넣어도 됨
                        PartnershipContractItem.Service.ByPeople(
                            opt.people,
                            opt.category
                        )
                    },
                    periodStart = item.partnershipPeriodStart.toString(),
                    periodEnd = item.partnershipPeriodEnd.toString()
                )

                val dialog = PartnershipContractDialogFragment.newInstance(contractData)
                dialog.show(fragmentManger, "PartnershipContractDialog")
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

}