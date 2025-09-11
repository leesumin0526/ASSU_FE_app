package com.example.assu_fe_app.domain.usecase.notification

import com.example.assu_fe_app.data.repository.notification.NotificationRepository
import com.example.assu_fe_app.util.RetrofitResult
import jakarta.inject.Inject

class GetUnreadExistsUseCase @Inject constructor(
    private val repo: NotificationRepository
) {
    suspend operator fun invoke(): RetrofitResult<Boolean> = repo.unreadExists()
}