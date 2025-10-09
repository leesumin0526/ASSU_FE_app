package com.ssu.assu.data.dto.admin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RandomPartnerResponseDto(
    val partnerId: Long,
    val partnerAddress: String,
    val partnerDetailAddress: String,
    val partnerName: String,
    val partnerUrl: String? = null,
    val partnerPhone: String? = null
)