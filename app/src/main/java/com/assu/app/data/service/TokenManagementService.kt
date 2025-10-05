package com.assu.app.data.service

import android.util.Log
import com.assu.app.data.local.AuthTokenLocalStore
import com.assu.app.data.repository.TokenRefreshRepository
import com.assu.app.util.RetrofitResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManagementService @Inject constructor(
    private val authTokenLocalStore: AuthTokenLocalStore,
    private val tokenRefreshRepository: TokenRefreshRepository
) {
    
    /**
     * 앱 시작 시 토큰 상태를 확인하고 필요시 갱신
     */
    fun checkAndRefreshTokenOnAppStart(scope: CoroutineScope) {
        Log.d("TokenManagementService", "=== APP START TOKEN CHECK ===")
        
        if (!authTokenLocalStore.isLoggedIn()) {
            Log.d("TokenManagementService", "User not logged in, skipping token check")
            return
        }
        
        val accessToken = authTokenLocalStore.getAccessToken()
        val refreshToken = authTokenLocalStore.getRefreshToken()
        val userRole = authTokenLocalStore.getUserRole()
        
        Log.d("TokenManagementService", "User logged in - Role: $userRole")
        Log.d("TokenManagementService", "Access token available: ${accessToken != null}")
        Log.d("TokenManagementService", "Refresh token available: ${refreshToken != null}")
        
        if (accessToken != null) {
            Log.d("TokenManagementService", "Access token (first 20 chars): ${accessToken.take(20)}...")
        }
        
        if (accessToken == null || refreshToken == null) {
            Log.w("TokenManagementService", "❌ Missing tokens, clearing auth data")
            authTokenLocalStore.clearTokens()
            return
        }
        
        // Access Token이 만료되었거나 곧 만료될 예정인지 확인
        val isExpiringSoon = authTokenLocalStore.isAccessTokenExpiringSoon()
        val isExpired = authTokenLocalStore.isAccessTokenExpired()
        
        Log.d("TokenManagementService", "Token expired: $isExpired")
        Log.d("TokenManagementService", "Token expiring soon: $isExpiringSoon")
        
        if (isExpiringSoon) {
            Log.i("TokenManagementService", "🔄 Access token expiring soon, refreshing in background...")
            refreshTokenInBackground(scope)
        } else {
            Log.d("TokenManagementService", "✅ Access token is still valid, no refresh needed")
        }
    }
    
    
    /**
     * 백그라운드에서 토큰 갱신
     */
    private fun refreshTokenInBackground(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            Log.d("TokenManagementService", "Starting background token refresh...")
            
            val result = tokenRefreshRepository.refreshToken()
            
            when (result) {
                is RetrofitResult.Success -> {
                    Log.i("TokenManagementService", "✅ Background token refresh successful")
                    Log.d("TokenManagementService", "New access token (first 20 chars): ${authTokenLocalStore.getAccessToken()?.take(20)}...")
                }
                is RetrofitResult.Fail -> {
                    Log.e("TokenManagementService", "❌ Background token refresh failed: ${result.message}")
                    Log.e("TokenManagementService", "Status code: ${result.statusCode}")
                    Log.w("TokenManagementService", "Clearing auth data due to refresh failure")
                    // 리프레시 실패 시 토큰 정리 (재로그인 필요)
                    authTokenLocalStore.clearTokens()
                }
                is RetrofitResult.Error -> {
                    Log.e("TokenManagementService", "❌ Background token refresh error: ${result.exception.message}")
                    Log.e("TokenManagementService", "Exception type: ${result.exception.javaClass.simpleName}")
                    // 네트워크 에러 등은 토큰을 정리하지 않음 (일시적 문제일 수 있음)
                }
            }
        }
    }
    
    /**
     * 수동으로 토큰 갱신 (필요시 호출 가능)
     */
    suspend fun refreshTokenManually(): RetrofitResult<Unit> {
        return tokenRefreshRepository.refreshToken()
    }
}
