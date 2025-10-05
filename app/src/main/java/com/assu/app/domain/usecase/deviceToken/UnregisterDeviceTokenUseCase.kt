package com.assu.app.domain.usecase.deviceToken

import com.assu.app.data.repository.deviceToken.DeviceTokenRepository
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class UnregisterDeviceTokenUseCase @Inject constructor(
    private val repo: DeviceTokenRepository
) {
    suspend operator fun invoke(tokenId: Long): RetrofitResult<String> =
        repo.unregister(tokenId)
}
