package com.assu.app.data.dto.auth

import com.google.gson.annotations.SerializedName

data class StudentTokenSignUpRequestDto(
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("marketingAgree")
    val marketingAgree: Boolean,
    @SerializedName("locationAgree")
    val locationAgree: Boolean,
    @SerializedName("studentTokenAuth")
    val studentTokenAuth: StudentTokenAuthPayloadDto
)

data class StudentTokenAuthPayloadDto(
    @SerializedName("sToken")
    val sToken: String,
    @SerializedName("sIdno")
    val sIdno: String,
    @SerializedName("university")
    val university: String = "SSU"
)
