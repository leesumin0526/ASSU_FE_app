package com.assu.app.presentation.common.contract

import com.assu.app.data.dto.partnership.PartnershipContractData
import com.assu.app.data.dto.partner_admin.home.PartnershipContractItem
import com.assu.app.domain.model.partnership.PartnershipOptionModel
import com.assu.app.data.dto.partnership.response.CriterionType
import com.assu.app.data.dto.partnership.response.OptionType
import com.assu.app.domain.model.partnership.ProposalPartnerDetailsModel
import java.text.NumberFormat
import java.util.Locale

fun ProposalPartnerDetailsModel.toContractData(
    partnerNameFallback: String? = "업체",
    adminNameFallback: String? = "관리자",
    fallbackStart: String? = null,
    fallbackEnd: String? = null
): PartnershipContractData {

    val items: List<PartnershipContractItem> =
        options.mapNotNull { it.toContractItem() }

    val start = periodStart ?: (fallbackStart ?: "")
    val end   = periodEnd   ?: (fallbackEnd ?: "")

    return PartnershipContractData(
        partnerName = partnerNameFallback,
        adminName   = adminNameFallback ?: "관리자",
        options     = items,
        periodStart = start,
        periodEnd   = end
    )
}

private fun PartnershipOptionModel.toContractItem(): PartnershipContractItem? {
    // 표시용 품목 문자열: goods 이름 우선, 없으면 category
    val itemsText = when {
        goods.isNotEmpty() -> goods.joinToString(", ") { it.goodsName }
        category.isNotBlank() -> category
        else -> ""
    }
    val cost = changeLongToMoney(cost)

    return when (optionType) {
        OptionType.SERVICE -> when (criterionType) {
            CriterionType.HEADCOUNT ->
                PartnershipContractItem.Service.ByPeople(
                    minPeople = people,
                    items = itemsText
                )
            CriterionType.PRICE ->
                PartnershipContractItem.Service.ByAmount(
                    minAmount = cost,
                    items = itemsText
                )
        }

        OptionType.DISCOUNT -> when (criterionType) {
            CriterionType.HEADCOUNT ->
                PartnershipContractItem.Discount.ByPeople(
                    minPeople = people,
                    percent = discountRate.toInt()
                )
            CriterionType.PRICE ->
                PartnershipContractItem.Discount.ByAmount(
                    minAmount = cost,
                    percent = discountRate.toInt()
                )
        }
    }
}

private fun changeLongToMoney(cost: Long?): String {
    val formatter = NumberFormat.getNumberInstance(Locale.KOREA)
    return formatter.format(cost)
}