package com.example.assu_fe_app.data.dto.auth

import com.example.assu_fe_app.domain.model.auth.LoginModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StudentLoginResponseDto(
    val memberId: Long,
    val role: String,
    val status: String,
    val tokens: TokenDto
) {
    fun toModel() = LoginModel(
        accessToken = tokens.accessToken,
        refreshToken = tokens.refreshToken,
        userId = memberId,
        username = "",
        userRole = role,
        email = null,
        profileImageUrl = null,
        status = status
    )
}

@JsonClass(generateAdapter = true)
data class TokenDto(
    val accessToken: String,
    val refreshToken: String
)
