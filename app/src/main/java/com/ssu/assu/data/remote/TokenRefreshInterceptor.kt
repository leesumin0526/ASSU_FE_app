package com.ssu.assu.data.remote

import android.util.Log
import com.ssu.assu.data.local.AccessTokenProvider
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenRefreshInterceptor @Inject constructor(
    private val accessTokenProvider: AccessTokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        Log.d("TokenRefreshInterceptor", "=== TOKEN REFRESH REQUEST INTERCEPT ===")
        Log.d("TokenRefreshInterceptor", "URL: ${original.url}")

        // Authorization 헤더 추가
        val bearer = accessTokenProvider.bearer()
        Log.d("TokenRefreshInterceptor", "Bearer token available: ${bearer != null}")
        Log.d("TokenRefreshInterceptor", "Bearer token (first 20 chars): ${bearer?.take(20)}...")

        bearer?.let {
            if (original.header("Authorization").isNullOrBlank()) {
                builder.addHeader("Authorization", it)
                Log.d("TokenRefreshInterceptor", "Authorization header added successfully")
            } else {
                Log.d("TokenRefreshInterceptor", "Authorization header already exists, skipping")
            }
        } ?: Log.w("TokenRefreshInterceptor", "No bearer token available - request will be sent without auth")

        val request = builder.build()
        Log.d("TokenRefreshInterceptor", "Sending request with headers: ${request.headers}")
        
        val response = chain.proceed(request)
        
        Log.d("TokenRefreshInterceptor", "=== TOKEN REFRESH RESPONSE ===")
        Log.d("TokenRefreshInterceptor", "Response code: ${response.code}")
        Log.d("TokenRefreshInterceptor", "Response headers: ${response.headers}")
        
        return response
    }
}
