package com.ssu.assu.data.dto.review.response

import com.ssu.assu.data.dto.common.Pageable
import com.ssu.assu.data.dto.common.SortX

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