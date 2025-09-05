package com.example.assu_fe_app.data

import com.example.assu_fe_app.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

// 🔴 임시
class DevBearerInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = BuildConfig.DEV_BEARER
        val req = if (token.isNotBlank()) {
            chain.request().newBuilder()
                .header("Authorization", token)
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(req)
    }
}

