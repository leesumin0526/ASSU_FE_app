package com.example.assu_fe_app.data

import com.example.assu_fe_app.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

// 🔴 임시
class DevBearerInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdXRoUmVhbG0iOiJTU1UiLCJyb2xlIjoiU1RVREVOVCIsInVzZXJJZCI6NiwidXNlcm5hbWUiOiIyMDI0MTY5MyIsImp0aSI6ImY2MjkyMmI1LWM5YzUtNDFiMS1iYTU0LTllMzFiZGVlZjcxMyIsImlhdCI6MTc1NzU5MjExMiwiZXhwIjoxNzU3NTk1NzEyfQ.MVQ-ocFTFKJ8yzjxjImrUo2SE8NRtHzmAx6qnc0p5XE"
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

