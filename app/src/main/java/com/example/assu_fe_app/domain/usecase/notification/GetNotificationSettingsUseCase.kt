package com.example.assu_fe_app.domain.usecase.notification

import com.example.assu_fe_app.data.repository.notification.NotificationRepository
import javax.inject.Inject

class GetNotificationSettingsUseCase @Inject constructor(
    private val repo: NotificationRepository
) {
    suspend operator fun invoke() = repo.getSettings()
}