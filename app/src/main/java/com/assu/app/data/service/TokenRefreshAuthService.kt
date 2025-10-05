package com.assu.app.data.service

import com.assu.app.data.dto.BaseResponse
import com.assu.app.data.dto.auth.RefreshResponseDto
import retrofit2.http.Header
import retrofit2.http.POST

interface TokenRefreshAuthService {
    
    @POST("auth/tokens/refresh")
    suspend fun refreshToken(
        @Header("RefreshToken") refreshToken: String
    ): BaseResponse<RefreshResponseDto>
}
