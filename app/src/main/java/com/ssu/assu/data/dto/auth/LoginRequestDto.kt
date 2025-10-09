package com.ssu.assu.data.dto.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StudentLoginRequestDto(
    val university: String,
    val sToken: String,
    val sIdno: String
)
