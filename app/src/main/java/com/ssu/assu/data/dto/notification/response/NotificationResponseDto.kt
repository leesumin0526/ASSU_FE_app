package com.ssu.assu.data.dto.notification.response

import com.ssu.assu.domain.model.notification.NotificationModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationResponseDto(
    val id: Long,
    val type: String,
    val refId: Long?,
    val title: String?,
    val messagePreview: String?,
    val deeplink: String?,
    @Json(name = "read") val isRead: Boolean,
    val createdAt: String?,  // "2025-09-09T20:12:34" 예상
    val readAt: String?,     // null 가능
    val timeAgo: String?     // "10분 전" 같은 표시 문자열
){
    fun toModel() = NotificationModel(
        id        = this.id,
        type      = this.type,
        refId     = this.refId,
        title     = this.title,
        preview   = this.messagePreview,
        deeplink  = this.deeplink,
        isRead    = this.isRead,
        createdAt = this.createdAt,
        readAt    = this.readAt,
        timeAgo   = this.timeAgo
    )
}