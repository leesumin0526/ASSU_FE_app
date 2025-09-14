package com.example.assu_fe_app.data

import com.example.assu_fe_app.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

// 🔴 임시
class DevBearerInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdXRoUmVhbG0iOiJDT01NT04iLCJyb2xlIjoiUEFSVE5FUiIsInVzZXJJZCI6MzksInVzZXJuYW1lIjoicGFydG5lcjFAZ21haWwuY29tIiwianRpIjoiODJjYjA2ZmMtMDhiOC00MDljLThlYTktOTc4Njk4NjM3MjY1IiwiaWF0IjoxNzU3ODM2MTkyLCJleHAiOjE3NTc4Mzk3OTJ9.JMsOyh5oHUoZAjsG5EcaqCdwvGaVEUnugtrXA9tHOtk"
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

