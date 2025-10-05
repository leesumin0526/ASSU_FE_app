package com.assu.app.data.dto.review.response

import com.assu.app.data.dto.common.Pageable
import com.assu.app.data.dto.common.SortX

data class GetReviewResponseDto(
    val content: List<ReviewResponseContent>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: SortX,
    val totalElements: Long,
    val totalPages: Int
)