package com.ssu.assu.domain.usecase.notification

import com.ssu.assu.data.repository.notification.NotificationRepository
import javax.inject.Inject

class GetNotificationSettingsUseCase @Inject constructor(
    private val repo: NotificationRepository
) {
    suspend operator fun invoke() = repo.getSettings()
}