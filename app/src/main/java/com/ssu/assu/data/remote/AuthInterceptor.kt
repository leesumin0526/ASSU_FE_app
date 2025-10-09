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
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val accessTokenProvider: AccessTokenProvider,
    private val tokenRefreshRepository: TokenRefreshRepository,
    private val authTokenLocalStore: AuthTokenLocalStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        
        // LoginActivity에서의 요청인지 확인 (Context 체크)
        val context = MyApplication.getApplicationContext()
        if (context != null) {
            try {
                val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
                val runningTasks = activityManager.getRunningTasks(1)
                if (runningTasks.isNotEmpty()) {
                    val topActivity = runningTasks[0].topActivity
                    if (topActivity != null && topActivity.className.contains("LoginActivity")) {
                        Log.d("AuthInterceptor", "Request from LoginActivity - skipping auth interceptor")
                        return chain.proceed(original)
                    }
                }
            } catch (e: Exception) {
                Log.w("AuthInterceptor", "Could not check current activity: ${e.message}")
            }
        }
        
        val builder = original.newBuilder()

        Log.d("AuthInterceptor", "=== REQUEST INTERCEPT ===")
        Log.d("AuthInterceptor", "URL: ${original.url}")
        Log.d("AuthInterceptor", "Method: ${original.method}")

        val bearer = accessTokenProvider.bearer()
        Log.d("AuthInterceptor", "Bearer token available: ${bearer != null}")
        Log.d("AuthInterceptor", "Bearer token (first 20 chars): ${bearer?.take(20)}...")

        bearer?.let {
            // 이미 Authorization 헤더가 있으면 덮어쓰지 않도록 하고 싶다면 조건 추가
            if (original.header("Authorization").isNullOrBlank()) {
                builder.addHeader("Authorization", it)
                Log.d("AuthInterceptor", "Authorization header added successfully")
            } else {
                Log.d("AuthInterceptor", "Authorization header already exists, skipping")
            }
        } ?: Log.w("AuthInterceptor", "No bearer token available - request will be sent without auth")

        val request = builder.build()
        Log.d("AuthInterceptor", "Sending request with headers: ${request.headers}")
        
        val response = chain.proceed(request)
        
        Log.d("AuthInterceptor", "=== RESPONSE RECEIVED ===")
        Log.d("AuthInterceptor", "Response code: ${response.code}")
        Log.d("AuthInterceptor", "Response headers: ${response.headers}")
        
        // 로그인/회원가입/토큰 관련 API는 제외 (무한 루프 방지)
        val isAuthRelatedApi = original.url.toString().contains("/auth/") || 
                              original.url.toString().contains("/login") ||
                              original.url.toString().contains("/signup") ||
                              original.url.toString().contains("/tokens/refresh")
        
        // LoginActivity에서의 API 호출인지 확인 (추가 보호)
        val isFromLoginActivity = original.header("X-From-Login-Activity") == "true"
        
        // 401 Unauthorized 또는 403 Forbidden 응답이면 토큰 리프레시 시도 (인증 관련 API 및 LoginActivity 제외)
        if ((response.code == 401 || response.code == 403) && !isAuthRelatedApi && !isFromLoginActivity) {
            Log.w("AuthInterceptor", "Received ${response.code} - Token may be expired or invalid")
            Log.d("AuthInterceptor", "=== ATTEMPTING TOKEN REFRESH ===")
            
            val refreshResult = runBlocking {
                Log.d("AuthInterceptor", "Calling tokenRefreshRepository.refreshToken()")
                tokenRefreshRepository.refreshToken()
            }
            
            when (refreshResult) {
                is RetrofitResult.Success -> {
                    Log.i("AuthInterceptor", "✅ Token refresh SUCCESSFUL - Retrying original request")
                    
                    // 새로운 토큰으로 원래 요청 재시도
                    val newBearer = accessTokenProvider.bearer()
                    val retryBuilder = original.newBuilder()
                    
                    newBearer?.let { token ->
                        retryBuilder.removeHeader("Authorization")
                        retryBuilder.addHeader("Authorization", token)
                        Log.d("AuthInterceptor", "New token (first 20 chars): ${token.take(20)}...")
                        Log.d("AuthInterceptor", "Retrying original request with new token")
                        
                        response.close() // 원래 응답 닫기
                        val retryResponse = chain.proceed(retryBuilder.build())
                        
                        Log.d("AuthInterceptor", "=== RETRY RESPONSE ===")
                        Log.d("AuthInterceptor", "Retry response code: ${retryResponse.code}")
                        Log.i("AuthInterceptor", "✅ Original request succeeded after token refresh")
                        
                        return retryResponse
                    } ?: Log.e("AuthInterceptor", "❌ New token is null after successful refresh")
                }
                is RetrofitResult.Fail -> {
                    Log.e("AuthInterceptor", "❌ Token refresh FAILED: ${refreshResult.message}")
                    Log.e("AuthInterceptor", "Status code: ${refreshResult.statusCode}")
                    
                    // 리프레시 토큰도 만료된 경우 로그아웃 처리
                    if (refreshResult.statusCode == 401 || refreshResult.statusCode == 403) {
                        Log.w("AuthInterceptor", "Refresh token expired - performing logout")
                        performLogout()
                    }
                }
                is RetrofitResult.Error -> {
                    Log.e("AuthInterceptor", "❌ Token refresh ERROR: ${refreshResult.exception.message}")
                    Log.e("AuthInterceptor", "Exception type: ${refreshResult.exception.javaClass.simpleName}")
                    
                    // 네트워크 오류가 아닌 경우 로그아웃 처리
                    val isNetworkError = refreshResult.exception.message?.contains("network", ignoreCase = true) ?: false
                    if (!isNetworkError) {
                        Log.w("AuthInterceptor", "Token refresh error - performing logout")
                        performLogout()
                    }
                }
            }
        } else {
            Log.d("AuthInterceptor", "Response code ${response.code} - No token refresh needed")
        }
        
        Log.d("AuthInterceptor", "=== REQUEST COMPLETED ===")
        return response
    }
    
    private fun performLogout() {
        try {
            Log.d("AuthInterceptor", "=== PERFORMING LOGOUT ===")
            
            // 로컬 토큰 삭제
            authTokenLocalStore.clearTokens()
            Log.d("AuthInterceptor", "✅ Local tokens cleared")
            
            // 로그인 액티비티로 이동 (Application Context 사용)
            val context = MyApplication.getApplicationContext()
            if (context != null) {
                val intent = Intent(context, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
                Log.d("AuthInterceptor", "✅ Redirected to login activity")
            } else {
                Log.e("AuthInterceptor", "❌ Application context is null - cannot start login activity")
            }
            
        } catch (e: Exception) {
            Log.e("AuthInterceptor", "❌ Error during logout: ${e.message}", e)
        }
    }
}