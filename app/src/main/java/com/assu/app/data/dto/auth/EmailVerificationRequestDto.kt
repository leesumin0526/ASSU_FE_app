package com.assu.app.data.dto.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EmailVerificationRequestDto(
    val email: String
)
