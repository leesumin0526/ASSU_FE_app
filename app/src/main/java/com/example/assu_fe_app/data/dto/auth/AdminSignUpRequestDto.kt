package com.example.assu_fe_app.data.dto.auth

import com.google.gson.annotations.SerializedName

data class AdminSignUpRequestDto(
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("marketingAgree")
    val marketingAgree: Boolean,
    @SerializedName("locationAgree")
    val locationAgree: Boolean,
    @SerializedName("commonAuth")
    val commonAuth: CommonAuthDto,
    @SerializedName("commonInfo")
    val commonInfo: CommonInfoDto
)

data class CommonAuthDto(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("department")
    val department: String?,
    @SerializedName("major")
    val major: String?,
    @SerializedName("university")
    val university: String
)

data class CommonInfoDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("detailAddress")
    val detailAddress: String,
    @SerializedName("selectedPlace")
    val selectedPlace: SelectedPlaceDto
)

data class SelectedPlaceDto(
    @SerializedName("placeId")
    val placeId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("roadAddress")
    val roadAddress: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)
