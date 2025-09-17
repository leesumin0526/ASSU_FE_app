package com.example.assu_fe_app.data.remote

import android.util.Log
import com.example.assu_fe_app.data.local.AccessTokenProvider
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val accessTokenProvider: AccessTokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        val bearer = accessTokenProvider.bearer()
        Log.d("AuthInterceptor", "Bearer token: $bearer")

        bearer?.let {
            // 이미 Authorization 헤더가 있으면 덮어쓰지 않도록 하고 싶다면 조건 추가
            if (original.header("Authorization").isNullOrBlank()) {
                builder.addHeader("Authorization", it)
                Log.d("AuthInterceptor", "Authorization header added: $it")
            } else {
                Log.d("AuthInterceptor", "Authorization header already exists, skipping")
            }
        } ?: Log.w("AuthInterceptor", "No bearer token available")

        return chain.proceed(builder.build())
    }
}