package com.ssu.assu.data.repositoryImpl.deviceToken

import com.ssu.assu.data.repository.deviceToken.DeviceTokenRepository
import com.ssu.assu.data.service.deviceToken.DeviceTokenService
import com.ssu.assu.util.apiHandler
import javax.inject.Inject

class DeviceTokenRepositoryImpl @Inject constructor(
    private val api: DeviceTokenService
) : DeviceTokenRepository {

    override suspend fun register(token: String) =
        apiHandler(
            execute = { api.registerToken(token) },
            mapper = { it }
        )

    override suspend fun unregister(tokenId: Long) =
        apiHandler(
            execute = { api.unregisterToken(tokenId) },
            mapper = { it }
        )
}