package com.assu.app.data.repositoryImpl.notification

import com.assu.app.data.dto.notification.response.NotificationsPageDto
import com.assu.app.data.repository.notification.NotificationRepository
import com.assu.app.data.service.notification.NotificationService
import com.assu.app.domain.model.notification.NotificationSettingsModel
import com.assu.app.domain.model.notification.NotificationTypeModel
import com.assu.app.domain.model.notification.NotificationsPageModel
import com.assu.app.util.RetrofitResult
import com.assu.app.util.apiHandler
import jakarta.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val api: NotificationService
) : NotificationRepository {

    override suspend fun toggle(type: NotificationTypeModel): RetrofitResult<NotificationSettingsModel> =
        apiHandler(
            execute = { api.toggle(type.path) },
            mapper  = { dto -> NotificationSettingsModel.from(dto.settings) }
        )

    override suspend fun getSettings(): RetrofitResult<NotificationSettingsModel> =
        apiHandler(
            execute = { api.getSettings() },
            mapper  = { dto -> NotificationSettingsModel.from(dto.settings) }
        )

    override suspend fun getNotifications(
        status: String,
        page: Int,
        size: Int
    ): RetrofitResult<NotificationsPageModel> =
        apiHandler(
            execute = {
                val res = api.getNotifications(status, page, size)
                android.util.Log.d("NOTI_REPO", "api raw response = $res")
                res
            },
            mapper  = { dto: NotificationsPageDto ->
                android.util.Log.d("NOTI_REPO", "mapped dto items=${dto.items.size}, page=${dto.page}")
                dto.toModel()
            }
        )

    override suspend fun markRead(notificationId: Long): RetrofitResult<Unit> =
        apiHandler(
            execute = { api.markRead(notificationId) },
            mapper  = {  }
        )

    override suspend fun unreadExists(): RetrofitResult<Boolean> =
        apiHandler(
            execute = { api.unreadExists() },
            mapper  = { it }
        )
}