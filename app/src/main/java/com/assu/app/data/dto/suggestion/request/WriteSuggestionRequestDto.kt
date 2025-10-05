package com.assu.app.data.dto.suggestion.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WriteSuggestionRequestDto(
    val adminId: Long,
    val storeName: String,
    val benefit : String
)