package com.assu.app.presentation.admin.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.assu.app.data.dto.partner_admin.home.PartnershipContractItem
import com.assu.app.data.dto.partnership.PartnershipContractData
import com.assu.app.data.dto.partnership.response.CriterionType
import com.assu.app.data.dto.partnership.response.OptionType
import com.assu.app.data.local.AuthTokenLocalStore
import com.assu.app.databinding.ItemAssociationListBinding
import com.assu.app.domain.model.admin.GetProposalPartnerListModel
import com.assu.app.presentation.common.contract.PartnershipContractDialogFragment
import java.text.NumberFormat
import java.util.Locale


class AdminPartnerListAdapter(
    private val items: MutableList<GetProposalPartnerListModel>,
    private val fragmentManger: FragmentManager,
    private val authTokenLocalStore: AuthTokenLocalStore
) : RecyclerView.Adapter<AdminPartnerListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAssociationListBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GetProposalPartnerListModel) {
            binding.tvAssociationName.text = item.storeName
            val option = item.options.firstOrNull()
            val cost = changeLongToMoney(option?.cost)
            binding.tvBenefitDescription.text = if (option != null) {
                when (option.optionType) {
                    OptionType.SERVICE -> when (option.criterionType) {
                        CriterionType.HEADCOUNT ->
                            "${option.people ?: 0}명 이상 인증 시 ${option.goods.firstOrNull()?.goodsName ?: "상품"} 제공"

                        CriterionType.PRICE ->
                            "${cost}원 이상 주문 시 ${option.goods.firstOrNull()?.goodsName ?: "상품"} 제공"
                    }

                    OptionType.DISCOUNT -> when (option.criterionType) {
                        CriterionType.HEADCOUNT ->
                            "${option.people ?: 0}명 이상 인증 시${option.discountRate ?: 0}% 할인"

                        CriterionType.PRICE ->
                            "${cost}원 이상 주문 시 ${option.discountRate ?: 0}% 할인"
                    }
                }
            } else {
                "제휴 혜택 없음"
            }
            binding.tvBenefitPeriod.text =
                "${item.partnershipPeriodStart} ~ ${item.partnershipPeriodEnd}"

            itemView.setOnClickListener {
                val contractData = PartnershipContractData(
                    partnerName = item.storeName,         // TODO: 파트너명으로 교체
                    adminName = authTokenLocalStore.getUserName() ,                     // ★ 여기서 어댑터 인자 사용
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
    private fun mapOptionToContractItem(opt: com.assu.app.domain.model.partnership.PartnershipOptionModel)
            : PartnershipContractItem {
        // goods를 보기 좋게 합침 (없으면 "상품")
        val goodsText = opt.goods.firstOrNull()?.goodsName ?: "상품"
        val people = opt.people ?: 0
        val cost = changeLongToMoney(opt.cost)
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
                        minAmount = cost,   // ByAmount가 Int면 toInt(), Long이면 그대로 사용
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
                        minAmount = cost,
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

    fun updateItems(newItems: List<GetProposalPartnerListModel>) {
        (items as MutableList).clear()
        (items as MutableList).addAll(newItems)
        notifyDataSetChanged()
    }

    private fun changeLongToMoney(cost: Long?): String {
        val formatter = NumberFormat.getNumberInstance(Locale.KOREA)
        return formatter.format(cost)
    }

}