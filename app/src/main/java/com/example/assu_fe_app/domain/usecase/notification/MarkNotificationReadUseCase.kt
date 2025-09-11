package com.example.assu_fe_app.domain.usecase.notification

import com.example.assu_fe_app.data.repository.notification.NotificationRepository
import com.example.assu_fe_app.util.RetrofitResult
import jakarta.inject.Inject

class MarkNotificationReadUseCase @Inject constructor(
    private val repo: NotificationRepository
) {
    suspend operator fun invoke(notificationId: Long): RetrofitResult<Unit> =
        repo.markRead(notificationId)
}