package com.example.assu_fe_app.data.repository.deviceToken

import com.example.assu_fe_app.util.RetrofitResult

interface DeviceTokenRepository {
    suspend fun register(token: String): RetrofitResult<Long>
    suspend fun unregisterToken(tokenId: Long): RetrofitResult<String>
}
