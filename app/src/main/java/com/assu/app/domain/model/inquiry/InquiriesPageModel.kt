package com.assu.app.domain.model.inquiry

data class InquiriesPageModel(
    val items: List<InquiryModel>,
    val page: Int,
    val size: Int,
    val totalPages: Int,
    val totalElements: Long
)