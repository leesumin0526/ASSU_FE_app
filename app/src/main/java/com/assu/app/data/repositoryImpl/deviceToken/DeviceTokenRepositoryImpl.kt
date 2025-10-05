package com.assu.app.data.repositoryImpl.deviceToken

import com.assu.app.data.repository.deviceToken.DeviceTokenRepository
import com.assu.app.data.service.deviceToken.DeviceTokenService
import com.assu.app.util.apiHandler
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