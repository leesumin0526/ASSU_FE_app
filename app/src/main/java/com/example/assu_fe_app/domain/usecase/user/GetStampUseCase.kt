package com.example.assu_fe_app.domain.usecase.user

import com.example.assu_fe_app.data.repository.notification.NotificationRepository
import com.example.assu_fe_app.data.repository.user.UserHomeRepository
import com.example.assu_fe_app.domain.model.notification.NotificationTypeModel
import com.example.assu_fe_app.util.RetrofitResult
import jakarta.inject.Inject

class GetStampUseCase @Inject constructor(
    private val repo: UserHomeRepository
){
    suspend operator fun invoke(): RetrofitResult<Int> {
        return repo.getStampCount()
    }
}