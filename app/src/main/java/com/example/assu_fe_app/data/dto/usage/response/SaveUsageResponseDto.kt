package com.example.assu_fe_app.data.dto.usage.response

data class SaveUsageResponseDto(
    val contents: List<Content>,
    val storeId: Long,
    val storeName: String
)