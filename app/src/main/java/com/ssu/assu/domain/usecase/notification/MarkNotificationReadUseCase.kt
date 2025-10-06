package com.ssu.assu.domain.usecase.notification

import com.ssu.assu.data.repository.notification.NotificationRepository
import com.ssu.assu.util.RetrofitResult
import jakarta.inject.Inject

class MarkNotificationReadUseCase @Inject constructor(
    private val repo: NotificationRepository
) {
    suspend operator fun invoke(notificationId: Long): RetrofitResult<Unit> =
        repo.markRead(notificationId)
}