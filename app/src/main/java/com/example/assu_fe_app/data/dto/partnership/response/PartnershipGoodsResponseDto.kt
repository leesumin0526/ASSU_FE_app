package com.example.assu_fe_app.data.dto.partnership.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PartnershipGoodsResponseDto(
    val goodsId: Long,
    val goodsName: String
)
