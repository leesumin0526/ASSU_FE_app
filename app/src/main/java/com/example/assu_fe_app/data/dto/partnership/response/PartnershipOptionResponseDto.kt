package com.example.assu_fe_app.data.dto.partnership.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PartnershipOptionResponseDto(
    val optionType: String,
    val criterionType: String,
    val people: Int?,
    val cost: Long?,
    val category: String,
    val discountRate: Long?,
    val goods: List<PartnershipGoodsResponseDto>
)
