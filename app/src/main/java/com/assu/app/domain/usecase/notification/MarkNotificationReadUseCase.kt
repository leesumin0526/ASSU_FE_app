package com.assu.app.domain.usecase.notification

import com.assu.app.data.repository.notification.NotificationRepository
import com.assu.app.util.RetrofitResult
import jakarta.inject.Inject

class MarkNotificationReadUseCase @Inject constructor(
    private val repo: NotificationRepository
) {
    suspend operator fun invoke(notificationId: Long): RetrofitResult<Unit> =
        repo.markRead(notificationId)
}