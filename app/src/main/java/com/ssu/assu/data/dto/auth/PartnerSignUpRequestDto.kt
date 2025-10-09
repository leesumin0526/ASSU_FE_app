package com.ssu.assu.data.dto.auth

import com.google.gson.annotations.SerializedName

data class PartnerSignUpRequestDto(
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("marketingAgree")
    val marketingAgree: Boolean,
    @SerializedName("locationAgree")
    val locationAgree: Boolean,
    @SerializedName("commonAuth")
    val commonAuth: PartnerCommonAuthDto,
    @SerializedName("commonInfo")
    val commonInfo: CommonInfoDto
)

data class PartnerCommonAuthDto(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)
