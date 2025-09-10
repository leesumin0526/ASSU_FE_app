package com.example.assu_fe_app.data.repository.notification

import com.example.assu_fe_app.data.dto.notification.response.NotificationsPageDto
import com.example.assu_fe_app.domain.model.notification.NotificationSettingsModel
import com.example.assu_fe_app.domain.model.notification.NotificationTypeModel
import com.example.assu_fe_app.domain.model.notification.NotificationsPageModel
import com.example.assu_fe_app.util.RetrofitResult

interface NotificationRepository {
    suspend fun toggle(type: NotificationTypeModel): RetrofitResult<NotificationSettingsModel>
    suspend fun getSettings(): RetrofitResult<NotificationSettingsModel>
    suspend fun getNotifications(status: String, page: Int, size: Int): RetrofitResult<NotificationsPageModel>
    suspend fun markRead(notificationId: Long): RetrofitResult<Unit>
    suspend fun unreadExists(): RetrofitResult<Boolean>
}