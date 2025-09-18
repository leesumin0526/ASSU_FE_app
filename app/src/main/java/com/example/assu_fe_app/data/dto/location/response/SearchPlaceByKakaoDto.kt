package com.example.assu_fe_app.data.dto.location.response

data class SearchPlaceByKakaoDto(
    val address: String,
    val category: String,
    val distance: Int?,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val phone: String,
    val placeId: String,
    val placeUrl: String,
    val roadAddress: String
)