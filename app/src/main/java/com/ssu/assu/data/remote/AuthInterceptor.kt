package com.ssu.assu.data.remote

import android.content.Context
import android.content.Intent
import android.util.Log
import com.ssu.assu.MyApplication
import com.ssu.assu.data.local.AccessTokenProvider
import com.ssu.assu.data.local.AuthTokenLocalStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val accessTokenProvider: AccessTokenProvider,
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
        
        // response 헤더에 Authorization 키 값 존재하면 Access Token 업데이트
        val newAccessToken = response.header("Authorization")
        if (!newAccessToken.isNullOrBlank()) {
            // "Bearer " 접두사 제거
            val token = newAccessToken.removePrefix("Bearer ").trim()
            if (token.isNotEmpty()) {
                Log.d("AuthInterceptor", "New access token found in response header - updating")
                authTokenLocalStore.updateAccessToken(token)
                Log.i("AuthInterceptor", "✅ Access token updated from response header")
            }
        }
        
        Log.d("AuthInterceptor", "=== REQUEST COMPLETED ===")
        return response
    }
}