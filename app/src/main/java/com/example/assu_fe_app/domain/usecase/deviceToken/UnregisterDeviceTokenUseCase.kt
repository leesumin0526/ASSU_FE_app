package com.example.assu_fe_app.domain.usecase.deviceToken

import com.example.assu_fe_app.data.repository.deviceToken.DeviceTokenRepository
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class UnregisterDeviceTokenUseCase @Inject constructor(
    private val repo: DeviceTokenRepository
) {
    suspend operator fun invoke(tokenId: Long): RetrofitResult<String> =
        repo.unregister(tokenId)
}
