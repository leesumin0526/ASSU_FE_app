package com.example.assu_fe_app.data.dto.auth

import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class RefreshResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: LocalDateTime
)
