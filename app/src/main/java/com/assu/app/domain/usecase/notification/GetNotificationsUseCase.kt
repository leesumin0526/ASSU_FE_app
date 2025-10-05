package com.assu.app.domain.usecase.notification

import com.assu.app.data.repository.notification.NotificationRepository
import com.assu.app.domain.model.notification.NotificationsPageModel
import com.assu.app.util.RetrofitResult
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