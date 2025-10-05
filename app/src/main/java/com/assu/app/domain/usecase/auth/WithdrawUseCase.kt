package com.assu.app.domain.usecase.auth

import com.assu.app.data.repository.auth.AuthRepository
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class WithdrawUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): RetrofitResult<Unit> =
        authRepository.withdraw()
}
