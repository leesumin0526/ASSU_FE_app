package com.example.assu_fe_app.data

import com.example.assu_fe_app.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

// 🔴 임시
class DevBearerInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdXRoUmVhbG0iOiJDT01NT04iLCJyb2xlIjoiUEFSVE5FUiIsInVzZXJJZCI6MTYsInVzZXJuYW1lIjoicGFydG5lckBnbWFpbC5jb20iLCJqdGkiOiI2ODg1NDFkZC04OTg4LTQyY2QtOTVmMy1jZDc0ZGUxZmYwNTgiLCJpYXQiOjE3NTc1OTI3OTEsImV4cCI6MTc1NzU5NjM5MX0.kiFAim_VHuSe2AC-tmnMuC2m0v_E5NyJ6RnqC_T3fGA"
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

