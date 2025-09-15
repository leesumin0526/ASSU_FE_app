package com.example.assu_fe_app.data.dto.common

import com.example.assu_fe_app.data.dto.common.SortX

data class Pageable(
    val offset: Long,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val sort: SortX,
    val unpaged: Boolean
)