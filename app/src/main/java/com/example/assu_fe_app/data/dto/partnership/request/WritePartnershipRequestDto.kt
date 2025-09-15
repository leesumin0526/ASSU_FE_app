package com.example.assu_fe_app.data.dto.partnership.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WritePartnershipRequestDto(
    val paperId: Long,
    val partnershipPeriodStart: String,
    val partnershipPeriodEnd: String,
    val options: List<PartnershipOptionRequestDto>
)
