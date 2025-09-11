package com.example.assu_fe_app.domain.usecase.notification

import com.example.assu_fe_app.data.repository.notification.NotificationRepository
import com.example.assu_fe_app.domain.model.notification.NotificationsPageModel
import com.example.assu_fe_app.util.RetrofitResult
import jakarta.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val repo: NotificationRepository
) {
    suspend operator fun invoke(
        status: String,
        page: Int,
        size: Int
    ): RetrofitResult<NotificationsPageModel> = repo.getNotifications(status, page, size)
}