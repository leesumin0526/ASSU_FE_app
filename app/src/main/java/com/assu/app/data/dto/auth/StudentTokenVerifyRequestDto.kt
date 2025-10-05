package com.assu.app.data.dto.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StudentTokenVerifyRequestDto(
    val sToken: String,
    val sIdno: String
)
