package com.ssu.assu.data.dto.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TokenDto(
    val accessToken: String,
    val refreshToken: String
)
