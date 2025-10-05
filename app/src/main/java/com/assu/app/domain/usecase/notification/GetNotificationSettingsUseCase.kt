package com.assu.app.domain.usecase.notification

import com.assu.app.data.repository.notification.NotificationRepository
import javax.inject.Inject

class GetNotificationSettingsUseCase @Inject constructor(
    private val repo: NotificationRepository
) {
    suspend operator fun invoke() = repo.getSettings()
}