package com.ssu.assu.data.dto.partnership.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PartnershipGoodsRequestDto(
    val goodsName: String
)
