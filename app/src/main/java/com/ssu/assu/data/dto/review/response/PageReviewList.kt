package com.ssu.assu.data.dto.review.response

import com.ssu.assu.data.dto.review.Review

data class PageReviewList(
    val reviews: List<Review>,
    val isLastPage: Boolean
)