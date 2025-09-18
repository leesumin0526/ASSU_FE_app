package com.example.assu_fe_app.data.dto.user.home

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class StampResponseDto(
    val userId: Long,
    val stamp: Int,
    val message: String
)