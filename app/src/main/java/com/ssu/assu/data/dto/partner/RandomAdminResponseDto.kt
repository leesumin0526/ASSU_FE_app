package com.ssu.assu.data.dto.partner

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RandomAdminResponseDto(
    val admins: List<AdminLiteDto>
)

@JsonClass(generateAdapter = true)
data class AdminLiteDto(
    val adminId: Long,
    val adminAddress: String,
    val adminDetailAddress: String,
    val adminName: String,
    val adminUrl: String? = null,
    val adminPhone: String? = null
)