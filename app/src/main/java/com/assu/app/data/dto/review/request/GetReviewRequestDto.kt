package com.assu.app.data.dto.review.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetReviewRequestDto(
    val page: Int,
    val size: Int,
    val sort: String
)