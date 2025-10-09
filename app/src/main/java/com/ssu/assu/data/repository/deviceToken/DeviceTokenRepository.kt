package com.ssu.assu.data.repository.deviceToken

import com.ssu.assu.util.RetrofitResult

interface DeviceTokenRepository {
    suspend fun register(token: String): RetrofitResult<Long>
    suspend fun unregister(tokenId: Long): RetrofitResult<String>
}
