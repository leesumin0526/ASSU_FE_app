package com.example.assu_fe_app.data.dto.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StudentLoginRequestDto(
    val university: String,
    val sToken: String,
    val sIdno: String
)
