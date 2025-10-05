package com.assu.app.domain.usecase.notification

import com.assu.app.data.repository.notification.NotificationRepository
import com.assu.app.domain.model.notification.NotificationTypeModel
import jakarta.inject.Inject

class ToggleNotificationUseCase @Inject constructor(
    private val repo: NotificationRepository
) {
    suspend operator fun invoke(type: NotificationTypeModel) = repo.toggle(type)
}