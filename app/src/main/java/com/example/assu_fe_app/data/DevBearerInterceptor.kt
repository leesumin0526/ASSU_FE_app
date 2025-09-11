package com.example.assu_fe_app.data

import com.example.assu_fe_app.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

// 🔴 임시
class DevBearerInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdXRoUmVhbG0iOiJDT01NT04iLCJyb2xlIjoiQURNSU4iLCJ1c2VySWQiOjM4LCJ1c2VybmFtZSI6ImFkbWluM0BnbWFpbC5jb20iLCJqdGkiOiIzNzM1N2ViZi00NWRiLTRmNDgtODA1MS05ZDI4NjRkNjA5NDkiLCJpYXQiOjE3NTc1OTA3ODMsImV4cCI6MTc1NzU5NDM4M30.FoNlGHaRgOtr65-Lb_uUG1xue9tGNXw6ZMYXtD4R1-o"
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

