package com.example.assu_fe_app.domain.usecase.notification

import com.example.assu_fe_app.data.repository.notification.NotificationRepository
import com.example.assu_fe_app.domain.model.notification.NotificationTypeModel
import jakarta.inject.Inject

class ToggleNotificationUseCase @Inject constructor(
    private val repo: NotificationRepository
) {
    suspend operator fun invoke(type: NotificationTypeModel) = repo.toggle(type)
}