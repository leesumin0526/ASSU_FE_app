package com.assu.app.domain.model.notification

data class NotificationsPageModel(
    val items: List<NotificationModel>,
    val page: Int,
    val size: Int,
    val totalPages: Int,
    val totalElements: Long
)
