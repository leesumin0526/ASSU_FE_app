package com.ssu.assu.data.dto.user.home

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class StampResponseDto(
    val userId: Long,
    val stamp: Int,
    val message: String
)