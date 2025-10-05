package com.assu.app.data.dto.partnership.request

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
