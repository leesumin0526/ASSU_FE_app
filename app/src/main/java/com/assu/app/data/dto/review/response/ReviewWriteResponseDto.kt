package com.assu.app.data.dto.review.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReviewWriteResponseDto(
    val content: String,
    val createdAt: String,
    val memberId: Long?,
    val rate: Int,
    val reviewId: Long,
    val reviewImageUrls: List<String>
)