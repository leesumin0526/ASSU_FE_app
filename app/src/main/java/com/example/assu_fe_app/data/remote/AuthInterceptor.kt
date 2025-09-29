package com.example.assu_fe_app.data.remote

import android.util.Log
import com.example.assu_fe_app.data.local.AccessTokenProvider
import com.example.assu_fe_app.data.repository.TokenRefreshRepository
import com.example.assu_fe_app.util.RetrofitResult
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val accessTokenProvider: AccessTokenProvider,
    private val tokenRefreshRepository: TokenRefreshRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
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
        
        // 401 Unauthorized 또는 403 Forbidden 응답이면 토큰 리프레시 시도
        if (response.code == 401 || response.code == 403) {
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
                    // 토큰 리프레시 실패 시 원래 응답 반환
                }
                is RetrofitResult.Error -> {
                    Log.e("AuthInterceptor", "❌ Token refresh ERROR: ${refreshResult.exception.message}")
                    Log.e("AuthInterceptor", "Exception type: ${refreshResult.exception.javaClass.simpleName}")
                    // 토큰 리프레시 에러 시 원래 응답 반환
                }
            }
        } else {
            Log.d("AuthInterceptor", "Response code ${response.code} - No token refresh needed")
        }
        
        Log.d("AuthInterceptor", "=== REQUEST COMPLETED ===")
        return response
    }
}