package com.ssu.assu.data.dto.usage.response

import com.ssu.assu.data.dto.common.Pageable
import com.ssu.assu.data.dto.common.SortX

data class GetUnreviewedUsageDto(
    val content: List<Detail>,
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