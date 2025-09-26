package com.example.assu_fe_app.data.dto.partnership

import java.util.UUID

enum class OptionType {
    SERVICE,
    DISCOUNT
}

enum class CriterionType {
    PRICE,
    HEADCOUNT
}

data class BenefitItem(
    val id: String = UUID.randomUUID().toString(),
    var optionType: OptionType = OptionType.SERVICE,
    val criterionType: CriterionType = CriterionType.PRICE,
    val criterionValue: String = "",
    val category: String = "",
    val discountRate: String = "",
    val goods: List<String> = emptyList(),
    val placeholder: String = "캔콜라"
)
