package com.ssu.assu.domain.model.partnership

import com.ssu.assu.data.dto.partnership.response.CriterionType
import com.ssu.assu.data.dto.partnership.response.OptionType

data class PartnershipOptionModel(
    val optionType: OptionType,
    val criterionType: CriterionType,
    val people: Int,
    val cost: Long,
    val category: String,
    val discountRate: Long,
    val goods: List<PartnershipGoodsModel>
)