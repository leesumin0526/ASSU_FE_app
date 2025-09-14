package com.example.assu_fe_app.data

import com.example.assu_fe_app.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

// 🔴 임시
class DevBearerInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdXRoUmVhbG0iOiJDT01NT04iLCJyb2xlIjoiUEFSVE5FUiIsInVzZXJJZCI6OSwidXNlcm5hbWUiOiJoYWNrQGdtYWlsLmNvbSIsImp0aSI6IjMwOWI4MjFiLWRhMjMtNDIzNC05N2Y0LWViNTBiNmZiMzExYiIsImlhdCI6MTc1NzgzNjYzNSwiZXhwIjoxNzU3ODQwMjM1fQ.8UbV6g45B3TW6zFFW96rR32qfRPok11YD4EW8-6J4Uk"
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

