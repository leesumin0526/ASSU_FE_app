package com.example.assu_fe_app.data.dto.admin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RandomPartnerResponseDto(
    val partnerId: Long,
    val partnerAddress: String,
    val partnerDetailAddress: String,
    val partnerName: String
)