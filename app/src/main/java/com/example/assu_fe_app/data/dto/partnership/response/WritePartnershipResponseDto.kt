package com.example.assu_fe_app.data.dto.partnership.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WritePartnershipResponseDto(
    val partnershipId: Long,
    val partnershipPeriodStart: String,
    val partnershipPeriodEnd: String,
    val adminId: Long,
    val partnerId: Long,
    val storeId: Long,
    val options: List<PartnershipOptionResponseDto>
)
