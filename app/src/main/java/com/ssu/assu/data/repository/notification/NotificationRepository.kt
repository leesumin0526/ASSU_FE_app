package com.ssu.assu.data.repository.notification

import com.ssu.assu.domain.model.notification.NotificationSettingsModel
import com.ssu.assu.domain.model.notification.NotificationTypeModel
import com.ssu.assu.domain.model.notification.NotificationsPageModel
import com.ssu.assu.util.RetrofitResult

interface NotificationRepository {
    suspend fun toggle(type: NotificationTypeModel): RetrofitResult<NotificationSettingsModel>
    suspend fun getSettings(): RetrofitResult<NotificationSettingsModel>
    suspend fun getNotifications(status: String, page: Int, size: Int): RetrofitResult<NotificationsPageModel>
    suspend fun markRead(notificationId: Long): RetrofitResult<Unit>
    suspend fun unreadExists(): RetrofitResult<Boolean>
}