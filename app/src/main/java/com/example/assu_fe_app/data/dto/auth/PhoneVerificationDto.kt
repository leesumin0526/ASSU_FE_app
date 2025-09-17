package com.example.assu_fe_app.data.dto.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhoneVerificationSendRequestDto(
    val phoneNumber: String
)

@JsonClass(generateAdapter = true)
data class PhoneVerificationVerifyRequestDto(
    val phoneNumber: String,
    val authNumber: String
)
