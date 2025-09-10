package com.example.assu_fe_app.data.repositoryImpl.deviceToken

import com.example.assu_fe_app.data.repository.deviceToken.DeviceTokenRepository
import com.example.assu_fe_app.data.service.deviceToken.DeviceTokenService
import com.example.assu_fe_app.util.apiHandler
import javax.inject.Inject

class DeviceTokenRepositoryImpl @Inject constructor(
    private val api: DeviceTokenService
) : DeviceTokenRepository {

    override suspend fun register(token: String) =
        apiHandler(
            execute = { api.registerToken(token) },
            mapper = { it }
        )

    override suspend fun unregisterToken(tokenId: Long) =
        apiHandler(
            execute = { api.unregisterToken(tokenId) },
            mapper = { it }
        )
}