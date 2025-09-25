package com.example.assu_fe_app.data.dto.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AdminSignUpRequestDto(
    val phoneNumber: String,
    val marketingAgree: Boolean,
    val locationAgree: Boolean,
    val commonAuth: CommonAuthDto,
    val commonInfo: CommonInfoDto
)

@JsonClass(generateAdapter = true)
data class CommonAuthDto(
    val email: String,
    val password: String,
    val department: String,
    val major: String,
    val university: String
)

@JsonClass(generateAdapter = true)
data class CommonInfoDto(
    val name: String,
    val detailAddress: String,
    val selectedPlace: SelectedPlaceDto
)

@JsonClass(generateAdapter = true)
data class SelectedPlaceDto(
    val placeId: String,
    val name: String,
    val address: String,
    val roadAddress: String,
    val latitude: Double,
    val longitude: Double
)
