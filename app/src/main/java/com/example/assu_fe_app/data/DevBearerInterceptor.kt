package com.example.assu_fe_app.data

import com.example.assu_fe_app.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

// 🔴 임시
class DevBearerInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdXRoUmVhbG0iOiJTU1UiLCJyb2xlIjoiU1RVREVOVCIsInVzZXJJZCI6NiwidXNlcm5hbWUiOiIyMDI0MTY5MyIsImp0aSI6IjQ4ZTRhYjczLWE3MjAtNDE4Yi05MmZjLTFiOTQ1ODlkMTEyOCIsImlhdCI6MTc1NjkxMzA5NywiZXhwIjoxNzU2OTE2Njk3fQ.iuYs_kGlWOMKGicGf6dcMwHgxmYVIuoXkZwjJnk7XEc"
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

