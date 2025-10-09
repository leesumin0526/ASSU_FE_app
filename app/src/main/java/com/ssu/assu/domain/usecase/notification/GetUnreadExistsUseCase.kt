package com.ssu.assu.domain.usecase.notification

import com.ssu.assu.data.repository.notification.NotificationRepository
import com.ssu.assu.util.RetrofitResult
import jakarta.inject.Inject

class GetUnreadExistsUseCase @Inject constructor(
    private val repo: NotificationRepository
) {
    suspend operator fun invoke(): RetrofitResult<Boolean> = repo.unreadExists()
}