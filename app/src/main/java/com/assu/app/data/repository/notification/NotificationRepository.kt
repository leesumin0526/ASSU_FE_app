package com.assu.app.data.repository.notification

import com.assu.app.domain.model.notification.NotificationSettingsModel
import com.assu.app.domain.model.notification.NotificationTypeModel
import com.assu.app.domain.model.notification.NotificationsPageModel
import com.assu.app.util.RetrofitResult

interface NotificationRepository {
    suspend fun toggle(type: NotificationTypeModel): RetrofitResult<NotificationSettingsModel>
    suspend fun getSettings(): RetrofitResult<NotificationSettingsModel>
    suspend fun getNotifications(status: String, page: Int, size: Int): RetrofitResult<NotificationsPageModel>
    suspend fun markRead(notificationId: Long): RetrofitResult<Unit>
    suspend fun unreadExists(): RetrofitResult<Boolean>
}