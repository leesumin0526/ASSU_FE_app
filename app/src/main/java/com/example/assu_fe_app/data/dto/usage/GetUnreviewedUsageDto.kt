package com.example.assu_fe_app.data.dto.usage

import com.example.assu_fe_app.data.dto.common.Pageable
import com.example.assu_fe_app.data.dto.common.SortX
import com.example.assu_fe_app.data.dto.usage.response.Detail

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