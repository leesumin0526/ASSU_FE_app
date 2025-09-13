package com.example.assu_fe_app.data.dto.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommonLoginRequestDto(
    val email: String,
    val password: String
)

