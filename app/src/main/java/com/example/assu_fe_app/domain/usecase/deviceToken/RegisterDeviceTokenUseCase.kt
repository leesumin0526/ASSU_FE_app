package com.example.assu_fe_app.domain.usecase.deviceToken

import com.example.assu_fe_app.data.repository.deviceToken.DeviceTokenRepository
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class RegisterDeviceTokenUseCase @Inject constructor(
    private val repo: DeviceTokenRepository
) {
    suspend operator fun invoke(token: String): RetrofitResult<String> =
        repo.register(token)
}