package com.ssu.assu.domain.usecase.notification

import com.ssu.assu.data.repository.notification.NotificationRepository
import com.ssu.assu.domain.model.notification.NotificationTypeModel
import jakarta.inject.Inject

class ToggleNotificationUseCase @Inject constructor(
    private val repo: NotificationRepository
) {
    suspend operator fun invoke(type: NotificationTypeModel) = repo.toggle(type)
}