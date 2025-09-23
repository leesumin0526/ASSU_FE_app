package com.example.assu_fe_app.data.dto.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PartnerSignUpRequestDto(
    val phoneNumber: String,
    val marketingAgree: Boolean,
    val locationAgree: Boolean,
    val commonAuth: PartnerCommonAuthDto,
    val commonInfo: CommonInfoDto
)

@JsonClass(generateAdapter = true)
data class PartnerCommonAuthDto(
    val email: String,
    val password: String
)
