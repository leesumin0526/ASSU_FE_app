package com.example.assu_fe_app.data.dto.partnership.request

import com.example.assu_fe_app.data.dto.partnership.CriterionType
import com.example.assu_fe_app.data.dto.partnership.OptionType
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PartnershipOptionRequestDto(
    val optionType: String,
    val criterionType: String,
    val people: Int?,
    val cost: Long?,
    val category: String,
    val discountRate: Long?,
    val goods: List<PartnershipGoodsRequestDto>
)
