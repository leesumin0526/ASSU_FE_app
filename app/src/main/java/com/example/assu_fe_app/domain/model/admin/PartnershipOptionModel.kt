package com.example.assu_fe_app.domain.model.admin

import com.example.assu_fe_app.data.dto.partnership.response.CriterionType
import com.example.assu_fe_app.data.dto.partnership.response.OptionType

data class PartnershipOptionModel(
    val optionType: OptionType,
    val criterionType: CriterionType,
    val people: Int,
    val cost: Long,
    val category: String,
    val discountRate: Long,
    val goods: List<PartnershipGoodsModel>
)
