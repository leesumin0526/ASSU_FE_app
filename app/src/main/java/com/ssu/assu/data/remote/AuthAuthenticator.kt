package com.ssu.assu.data.remote

import android.content.Context
import android.content.Intent
import android.util.Log
import com.ssu.assu.MyApplication
import com.ssu.assu.data.local.AccessTokenProvider
import com.ssu.assu.data.local.AuthTokenLocalStore
import com.ssu.assu.data.repository.TokenRefreshRepository
import com.ssu.assu.presentation.common.login.LoginActivity
import com.ssu.assu.util.RetrofitResult
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

/**
 * OkHttp3 Authenticator 구현
 * 401 http error code를 받았을 때 호출되는 클래스
 * Access Token 만료 시 Refresh Token으로 교체 후 API 재요청
 */
class AuthAuthenticator @Inject constructor(
    private val accessTokenProvider: AccessTokenProvider,
    private val tokenRefreshRepository: TokenRefreshRepository,
    private val authTokenLocalStore: AuthTokenLocalStore
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d("AuthAuthenticator", "=== AUTHENTICATE CALLED ===")
        Log.d("AuthAuthenticator", "Response code: ${response.code}")
        Log.d("AuthAuthenticator", "URL: ${response.request.url}")

        // 401 Unauthorized가 아니면 null 반환 (인증 처리 안 함)
        if (response.code != 401) {
            Log.d("AuthAuthenticator", "Response code is not 401 - skipping authentication")
            return null
        }

        // 로그인/회원가입/토큰 관련 API는 제외 (무한 루프 방지)
        val requestUrl = response.request.url.toString()
        val isAuthRelatedApi = requestUrl.contains("/auth/") ||
                requestUrl.contains("/login") ||
                requestUrl.contains("/signup") ||
                requestUrl.contains("/tokens/refresh")

        if (isAuthRelatedApi) {
            Log.d("AuthAuthenticator", "Auth-related API - skipping token refresh")
            return null
        }

        // 이미 재시도한 요청인지 확인 (무한 루프 방지)
        val priorAuth = responseCount(response)
        if (priorAuth >= 3) {
            Log.w("AuthAuthenticator", "Too many failed attempts - giving up")
            performLogout()
            return null
        }

        Log.w("AuthAuthenticator", "Received 401 - Attempting token refresh (attempt $priorAuth)")
        Log.d("AuthAuthenticator", "=== ATTEMPTING TOKEN REFRESH ===")

        val refreshResult = runBlocking {
            Log.d("AuthAuthenticator", "Calling tokenRefreshRepository.refreshToken()")
            tokenRefreshRepository.refreshToken()
        }

        return when (refreshResult) {
            is RetrofitResult.Success -> {
                Log.i("AuthAuthenticator", "✅ Token refresh SUCCESSFUL - Retrying original request")

                // 새로운 토큰으로 원래 요청 재시도
                val newBearer = accessTokenProvider.bearer()
                newBearer?.let { token ->
                    Log.d("AuthAuthenticator", "New token (first 20 chars): ${token.take(20)}...")
                    Log.d("AuthAuthenticator", "Retrying original request with new token")

                    // 원래 요청을 새로운 토큰으로 재생성
                    response.request.newBuilder()
                        .header("Authorization", token)
                        .build()
                } ?: run {
                    Log.e("AuthAuthenticator", "❌ New token is null after successful refresh")
                    performLogout()
                    null
                }
            }
            is RetrofitResult.Fail -> {
                Log.e("AuthAuthenticator", "❌ Token refresh FAILED: ${refreshResult.message}")
                Log.e("AuthAuthenticator", "Status code: ${refreshResult.statusCode}")

                // 리프레시 토큰도 만료된 경우 로그아웃 처리
                if (refreshResult.statusCode == 401 || refreshResult.statusCode == 403) {
                    Log.w("AuthAuthenticator", "Refresh token expired - performing logout")
                    performLogout()
                }
                null
            }
            is RetrofitResult.Error -> {
                Log.e("AuthAuthenticator", "❌ Token refresh ERROR: ${refreshResult.exception.message}")
                Log.e("AuthAuthenticator", "Exception type: ${refreshResult.exception.javaClass.simpleName}")

                // 네트워크 오류가 아닌 경우 로그아웃 처리
                val isNetworkError = refreshResult.exception.message?.contains("network", ignoreCase = true) ?: false
                if (!isNetworkError) {
                    Log.w("AuthAuthenticator", "Token refresh error - performing logout")
                    performLogout()
                }
                null
            }
        }
    }

    /**
     * 재시도 횟수 확인 (무한 루프 방지)
     */
    private fun responseCount(response: Response): Int {
        var result = 1
        var currentResponse: Response? = response
        while (currentResponse?.priorResponse != null) {
            result++
            currentResponse = currentResponse.priorResponse
        }
        return result
    }

    private fun performLogout() {
        try {
            Log.d("AuthAuthenticator", "=== PERFORMING LOGOUT ===")

            // 로컬 토큰 삭제
            authTokenLocalStore.clearTokens()
            Log.d("AuthAuthenticator", "✅ Local tokens cleared")

            // 로그인 액티비티로 이동 (Application Context 사용)
            val context = MyApplication.getApplicationContext()
            if (context != null) {
                val intent = Intent(context, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
                Log.d("AuthAuthenticator", "✅ Redirected to login activity")
            } else {
                Log.e("AuthAuthenticator", "❌ Application context is null - cannot start login activity")
            }

        } catch (e: Exception) {
            Log.e("AuthAuthenticator", "❌ Error during logout: ${e.message}", e)
        }
    }
}

