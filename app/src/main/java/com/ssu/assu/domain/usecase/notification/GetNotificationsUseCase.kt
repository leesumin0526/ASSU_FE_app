package com.ssu.assu.domain.usecase.notification

import com.ssu.assu.data.repository.notification.NotificationRepository
import com.ssu.assu.domain.model.notification.NotificationsPageModel
import com.ssu.assu.util.RetrofitResult
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