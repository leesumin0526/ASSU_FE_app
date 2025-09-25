package com.example.assu_fe_app.data.dto.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StudentTokenSignUpRequestDto(
    val phoneNumber: String,
    val marketingAgree: Boolean,
    val locationAgree: Boolean,
    val studentTokenAuth: StudentTokenAuthPayloadDto
)

@JsonClass(generateAdapter = true)
data class StudentTokenAuthPayloadDto(
    val sToken: String,
    val sIdno: String,
    val university: String = "SSU"
)
