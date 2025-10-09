package com.ssu.assu.data.dto.partnership.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SelectedPlaceDto(
    val placeId: String?,
    val name: String?,        // 장소명
    val address: String?,     // 지번
    val roadAddress: String?, // 도로명
    val latitude: Double?,    // 위도
    val longitude: Double?    // 경도
)