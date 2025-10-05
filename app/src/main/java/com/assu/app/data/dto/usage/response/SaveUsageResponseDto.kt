package com.assu.app.data.dto.usage.response

data class SaveUsageResponseDto(
    val contents: List<Content>,
    val storeId: Long,
    val storeName: String
)