package com.example.assu_fe_app.domain.usecase.auth

import com.example.assu_fe_app.data.repository.auth.AuthRepository
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): RetrofitResult<Unit> =
        authRepository.logout()
}
