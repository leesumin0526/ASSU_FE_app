package com.ssu.assu.domain.usecase.deviceToken

import com.ssu.assu.data.repository.deviceToken.DeviceTokenRepository
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class RegisterDeviceTokenUseCase @Inject constructor(
    private val repo: DeviceTokenRepository
) {
    suspend operator fun invoke(token: String): RetrofitResult<Long> =
        repo.register(token)
}