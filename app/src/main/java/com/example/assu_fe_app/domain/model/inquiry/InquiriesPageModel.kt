package com.example.assu_fe_app.domain.model.inquiry

data class InquiriesPageModel(
    val items: List<InquiryModel>,
    val page: Int,
    val size: Int,
    val totalPages: Int,
    val totalElements: Long
)