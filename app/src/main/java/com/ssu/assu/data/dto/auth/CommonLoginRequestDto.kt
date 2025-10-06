package com.ssu.assu.data.dto.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommonLoginRequestDto(
    val email: String,
    val password: String
)

