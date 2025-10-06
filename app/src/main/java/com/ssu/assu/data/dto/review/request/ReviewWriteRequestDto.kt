package com.ssu.assu.data.dto.review.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReviewWriteRequestDto(
    val adminName: String,
    val content: String,
    val partnerId: Long,
    val rate: Int,
    val storeId: Long,
    val partnershipUsageId: Long
)