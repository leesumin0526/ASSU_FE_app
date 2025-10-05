package com.assu.app.data.dto.partnership.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdatePartnershipStatusRequestDto(
    val status: String
)
