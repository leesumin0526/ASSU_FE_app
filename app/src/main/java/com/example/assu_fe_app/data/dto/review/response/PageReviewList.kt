package com.example.assu_fe_app.data.dto.review.response

import com.example.assu_fe_app.data.dto.review.Review

data class PageReviewList(
    val reviews: List<Review>,
    val isLastPage: Boolean
)