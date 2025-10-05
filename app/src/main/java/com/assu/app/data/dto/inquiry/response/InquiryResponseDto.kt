package com.assu.app.data.dto.inquiry.response

import com.assu.app.domain.model.inquiry.InquiryModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InquiryResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val email: String,
    val status: String,          // "WAITING" | "ANSWERED"
    val createdAt: String? = null,
    val answeredAt: String? = null,
    val answer: String? = null
){
    fun toModel() = InquiryModel(
        id = this.id,
        title = this.title,
        content = this.content,
        email = this.email,
        status = this.status,
        createdAt = this.createdAt,
        answeredAt = this.answeredAt,
        answer = this.answer
    )
}
