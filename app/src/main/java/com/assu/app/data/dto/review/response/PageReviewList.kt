package com.assu.app.data.dto.review.response

import com.assu.app.data.dto.review.Review

data class PageReviewList(
    val reviews: List<Review>,
    val isLastPage: Boolean
)