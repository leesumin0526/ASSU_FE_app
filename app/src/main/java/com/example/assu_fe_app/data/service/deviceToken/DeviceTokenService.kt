package com.example.assu_fe_app.data.service.deviceToken

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.deviceToken.request.DeviceTokenRequestDto
import retrofit2.http.Body
import retrofit2.http.POST


interface DeviceTokenService {
    @POST("device-tokens")
    suspend fun registerToken(
        @Body body: DeviceTokenRequestDto
    ): BaseResponse<String> // 서버 응답: BaseResponse<String>
}