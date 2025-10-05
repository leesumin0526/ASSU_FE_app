package com.assu.app.data.repository.deviceToken

import com.assu.app.util.RetrofitResult

interface DeviceTokenRepository {
    suspend fun register(token: String): RetrofitResult<Long>
    suspend fun unregister(tokenId: Long): RetrofitResult<String>
}
