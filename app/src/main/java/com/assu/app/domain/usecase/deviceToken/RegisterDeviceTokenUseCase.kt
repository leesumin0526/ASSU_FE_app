package com.assu.app.domain.usecase.deviceToken

import com.assu.app.data.repository.deviceToken.DeviceTokenRepository
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class RegisterDeviceTokenUseCase @Inject constructor(
    private val repo: DeviceTokenRepository
) {
    suspend operator fun invoke(token: String): RetrofitResult<Long> =
        repo.register(token)
}