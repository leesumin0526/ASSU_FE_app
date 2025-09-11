package com.example.assu_fe_app.data

import com.example.assu_fe_app.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

// 🔴 임시
class DevBearerInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdXRoUmVhbG0iOiJDT01NT04iLCJyb2xlIjoiQURNSU4iLCJ1c2VySWQiOjM4LCJ1c2VybmFtZSI6ImFkbWluM0BnbWFpbC5jb20iLCJqdGkiOiIyY2Y3ZDEzYy0xNWUzLTQ5N2QtOTU3Zi05MTc1MDI3NzVkZTgiLCJpYXQiOjE3NTc1NzA1NTAsImV4cCI6MTc1NzU3NDE1MH0.QMDTb7zUKKlgcifTbPaJbrO75CZPa3z4IK4tC1MaDUs"
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

