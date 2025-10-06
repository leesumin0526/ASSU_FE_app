package com.ssu.assu.domain.model.notification

data class NotificationModel(
    val id: Long,
    val type: String,
    val refId: Long?,
    val title: String?,
    val preview: String?,     // messagePreview → preview 로 앱 내 명칭 통일
    val deeplink: String?,
    val isRead: Boolean,
    val createdAt: String?,   // 필요 시 LocalDateTime로 파싱해서 별도 필드 추가 가능
    val readAt: String?,
    val timeAgo: String?
)
