package com.assu.app.data.dto

data class InquiryItem(
    val id: String,
    val title: String,
    val content: String,
    val email: String,
    val date: String,
    val time: String,
    val status: InquiryStatus,
    val answer: String? = null
)

enum class InquiryStatus {
    PENDING,
    COMPLETED
}
