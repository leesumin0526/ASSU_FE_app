package com.example.assu_fe_app.presentation.admin.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.data.dto.partner_admin.home.PartnershipContractItem
import com.example.assu_fe_app.data.dto.partnership.PartnershipContractData
import com.example.assu_fe_app.data.dto.partnership.response.CriterionType
import com.example.assu_fe_app.data.dto.partnership.response.OptionType
import com.example.assu_fe_app.databinding.ItemAssociationListBinding
import com.example.assu_fe_app.domain.model.admin.GetProposalPartnerListModel
import com.example.assu_fe_app.presentation.common.contract.PartnershipContractDialogFragment


class AdminPartnerListAdapter(
    private val items: List<GetProposalPartnerListModel>,
    private val fragmentManger: FragmentManager,
    private val adminName: String // TokenManager에서 불러온 관리자 이름
) : RecyclerView.Adapter<AdminPartnerListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAssociationListBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GetProposalPartnerListModel) {
            //TODO: 이름 바꾸기
            binding.tvAssociationName.text = item.partnerId.toString()
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
                    partnerName = item.partnerId.toString(),         // TODO: 파트너명으로 교체
                    adminName = item.adminId.toString(),                     // ★ 여기서 어댑터 인자 사용
                    options = item.options.map { mapOptionToContractItem(it) },
                    periodStart = item.partnershipPeriodStart.toString(),
                    periodEnd   = item.partnershipPeriodEnd.toString()
                )

                PartnershipContractDialogFragment
                    .newInstance(contractData)
                    .show(fragmentManger, "PartnershipContractDialog")
            }
        }
    }
    private fun mapOptionToContractItem(opt: com.example.assu_fe_app.domain.model.admin.PartnershipOptionModel)
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
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<GetProposalPartnerListModel>) {
        (items as MutableList).clear()
        (items as MutableList).addAll(newItems)
        notifyDataSetChanged()
    }

}