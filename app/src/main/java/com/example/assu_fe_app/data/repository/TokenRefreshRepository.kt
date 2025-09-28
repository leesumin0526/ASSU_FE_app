package com.example.assu_fe_app.data.repository

import android.util.Log
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import com.example.assu_fe_app.data.service.TokenRefreshAuthService
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRefreshRepository @Inject constructor(
    private val tokenRefreshAuthService: TokenRefreshAuthService,
    private val authTokenLocalStore: AuthTokenLocalStore
) {
    
    suspend fun refreshToken(): RetrofitResult<Unit> {
        Log.d("TokenRefreshRepository", "=== TOKEN REFRESH START ===")
        
        val refreshToken = authTokenLocalStore.getRefreshToken()
        if (refreshToken == null) {
            Log.e("TokenRefreshRepository", "❌ Refresh token not found in local storage")
            return RetrofitResult.Error(Exception("Refresh token not found"))
        }
        
        Log.d("TokenRefreshRepository", "Refresh token found (first 20 chars): ${refreshToken.take(20)}...")
        Log.d("TokenRefreshRepository", "Current access token (first 20 chars): ${authTokenLocalStore.getAccessToken()?.take(20)}...")
        
        return apiHandler(
            execute = { 
                Log.d("TokenRefreshRepository", "Calling tokenRefreshAuthService.refreshToken()")
                tokenRefreshAuthService.refreshToken(refreshToken) 
            },
            mapper = { refreshResponse ->
                Log.d("TokenRefreshRepository", "✅ Received new tokens from server")
                Log.d("TokenRefreshRepository", "New access token (first 20 chars): ${refreshResponse.accessToken.take(20)}...")
                Log.d("TokenRefreshRepository", "New refresh token (first 20 chars): ${refreshResponse.refreshToken.take(20)}...")
                Log.d("TokenRefreshRepository", "Token expires at: ${refreshResponse.expiresAt}")
                
                // 새로운 토큰들을 저장
                authTokenLocalStore.updateTokens(
                    accessToken = refreshResponse.accessToken,
                    refreshToken = refreshResponse.refreshToken
                )
                
                Log.i("TokenRefreshRepository", "✅ Tokens updated successfully in local storage")
                Unit
            }
        ).also { result ->
            when (result) {
                is RetrofitResult.Success -> {
                    Log.i("TokenRefreshRepository", "✅ TOKEN REFRESH SUCCESS")
                }
                is RetrofitResult.Fail -> {
                    Log.e("TokenRefreshRepository", "❌ TOKEN REFRESH FAILED: ${result.message}")
                    Log.e("TokenRefreshRepository", "Status code: ${result.statusCode}")
                }
                is RetrofitResult.Error -> {
                    Log.e("TokenRefreshRepository", "❌ TOKEN REFRESH ERROR: ${result.exception.message}")
                    Log.e("TokenRefreshRepository", "Exception type: ${result.exception.javaClass.simpleName}")
                }
            }
            Log.d("TokenRefreshRepository", "=== TOKEN REFRESH END ===")
        }
    }
}
