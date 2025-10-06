package com.ssu.assu.data.service

import com.ssu.assu.data.dto.BaseResponse
import com.ssu.assu.data.dto.auth.RefreshResponseDto
import retrofit2.http.Header
import retrofit2.http.POST

interface TokenRefreshAuthService {
    
    @POST("auth/tokens/refresh")
    suspend fun refreshToken(
        @Header("RefreshToken") refreshToken: String
    ): BaseResponse<RefreshResponseDto>
}
