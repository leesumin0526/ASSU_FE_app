package com.ssu.assu.domain.model.inquiry

data class InquiryModel(
    val id: Long,
    val title: String,
    val content: String,
    val email: String,
    val status: String,
    val createdAt: String? = null,
    val answeredAt: String? = null,
    val answer: String? = null
)