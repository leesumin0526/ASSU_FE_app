package com.ssu.assu.data.dto.common

data class Pageable(
    val offset: Long,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val sort: SortX,
    val unpaged: Boolean
)