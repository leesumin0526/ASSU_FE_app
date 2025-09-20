package com.example.assu_fe_app.presentation.partner.home.adapter

import com.example.assu_fe_app.databinding.ItemAssociationListBinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.presentation.common.contract.PartnershipContractDialogFragment
import com.example.assu_fe_app.data.dto.partner_admin.home.PartnershipContractItem
import com.example.assu_fe_app.data.dto.partnership.PartnershipContractData
import com.example.assu_fe_app.data.dto.partnership.response.CriterionType
import com.example.assu_fe_app.data.dto.partnership.response.OptionType
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import com.example.assu_fe_app.domain.model.admin.GetProposalAdminListModel
import com.example.assu_fe_app.domain.model.admin.GetProposalPartnerListModel
import com.example.assu_fe_app.domain.model.partnership.PartnershipOptionModel
import com.example.assu_fe_app.domain.model.partnership.ProposalPartnerDetailsModel


class PartnerAdminListAdapter(
    private val items: List<GetProposalAdminListModel>,
    private val fragmentManager: FragmentManager,
    private val authTokenLocalStore: AuthTokenLocalStore
) : RecyclerView.Adapter<PartnerAdminListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAssociationListBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GetProposalAdminListModel) {
            binding.tvAssociationName.text = item.adminName
            val option = item.options.firstOrNull()
            binding.tvBenefitDescription.text = if (option != null) {
                when (option.optionType) {
                    OptionType.SERVICE -> when (option.criterionType) {
                        CriterionType.HEADCOUNT ->
                            "${option.people ?: 0}명 이상 인증 시 ${option.goods.firstOrNull()?.goodsName ?: "상품"} 제공"

                        CriterionType.PRICE ->
                            "${option.cost ?: 0}원 이상 주문 시 ${option.goods.firstOrNull()?.goodsName ?: "상품"} 제공"
                    }

                    OptionType.DISCOUNT -> when (option.criterionType) {
                        CriterionType.HEADCOUNT ->
                            "${option.people ?: 0}명 이상 인증 시${option.discountRate ?: 0}% 할인"

                        CriterionType.PRICE ->
                            "${option.cost ?: 0}원 이상 주문 시 ${option.discountRate ?: 0}% 할인"
                    }
                }
            } else {
                "제휴 혜택 없음"
            }
            binding.tvBenefitPeriod.text =
                "${item.partnershipPeriodStart} ~ ${item.partnershipPeriodEnd}"

            itemView.setOnClickListener {
                val contractData = PartnershipContractData(
                    partnerName = authTokenLocalStore.getUserName(),         // TODO: 파트너명으로 교체
                    adminName = item.adminName,                   // ★ 여기서 어댑터 인자 사용
                    options = item.options.map { mapOptionToContractItem(it) },
                    periodStart = item.partnershipPeriodStart.toString(),
                    periodEnd = item.partnershipPeriodEnd.toString()
                )

                PartnershipContractDialogFragment
                    .newInstance(contractData)
                    .show(fragmentManager, "PartnershipContractDialog")
            }
        }
    }

    private fun mapOptionToContractItem(opt:PartnershipOptionModel)
            : PartnershipContractItem {
        // goods를 보기 좋게 합침 (없으면 "상품")
        val goodsText = opt.goods.firstOrNull()?.goodsName ?: "상품"
        val people = opt.people ?: 0
        val cost = (opt.cost ?: 0L)
        val discount = (opt.discountRate ?: 0L)

        return when (opt.optionType) {
            OptionType.SERVICE -> when (opt.criterionType) {
                CriterionType.HEADCOUNT ->
                    PartnershipContractItem.Service.ByPeople(
                        minPeople = people,
                        items = goodsText
                    )
                CriterionType.PRICE ->
                    PartnershipContractItem.Service.ByAmount(
                        minAmount = cost.toInt(),   // ByAmount가 Int면 toInt(), Long이면 그대로 사용
                        items = goodsText
                    )
            }

            OptionType.DISCOUNT -> when (opt.criterionType) {
                CriterionType.HEADCOUNT ->
                    PartnershipContractItem.Discount.ByPeople(
                        minPeople = people,
                        percent = discount.toInt()
                    )
                CriterionType.PRICE ->
                    PartnershipContractItem.Discount.ByAmount(
                        minAmount = cost.toInt(),
                        percent = discount.toInt()
                    )
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

    fun updateItems(newItems: List<GetProposalAdminListModel>) {
        (items as MutableList).clear()
        (items as MutableList).addAll(newItems)
        notifyDataSetChanged()
    }
}