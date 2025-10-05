package com.assu.app.data.service.deviceToken

import com.assu.app.data.dto.BaseResponse
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface DeviceTokenService {
    @POST("/device-tokens")
    suspend fun registerToken(
        @Query("token") token: String
    ): BaseResponse<Long>

    @DELETE("/device-tokens/{token-id}")
    suspend fun unregisterToken(
        @Path("token-id") tokenId: Long
    ): BaseResponse<String>
}