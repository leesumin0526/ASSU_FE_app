package com.assu.app.data.dto.inquiry.response

import com.assu.app.domain.model.inquiry.InquiriesPageModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InquiriesPageDto(
    val items: List<InquiryResponseDto>,
    val page: Int,
    val size: Int,
    val totalPages: Int,
    val totalElements: Long
){
    fun toModel() = InquiriesPageModel(
        items = items.map { it.toModel() },
        page = page,
        size = size,
        totalPages = totalPages,
        totalElements = totalElements
    )
}