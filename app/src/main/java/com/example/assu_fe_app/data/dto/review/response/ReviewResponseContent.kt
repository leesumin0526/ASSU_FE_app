package com.example.assu_fe_app.data.dto.review.response

data class ReviewResponseContent(
    val affiliation:String,
    val content: String,
    val createdAt: String,
    val rate: Int,
    val reviewId: Long,
    val reviewImageUrls: List<String>,
    val storeId: Long,
    val storeName: String
)