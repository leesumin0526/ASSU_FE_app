package com.assu.app.data.dto.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RefreshResponseDto(
    val memberId: Long,
    val newAccess: String,
    val newRefresh: String
)
