package com.ssu.assu.domain.usecase.deviceToken

import com.ssu.assu.data.repository.deviceToken.DeviceTokenRepository
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class UnregisterDeviceTokenUseCase @Inject constructor(
    private val repo: DeviceTokenRepository
) {
    suspend operator fun invoke(tokenId: Long): RetrofitResult<String> =
        repo.unregister(tokenId)
}
