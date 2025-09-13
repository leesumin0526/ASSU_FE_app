package com.example.assu_fe_app.data.local

interface TokenProvider {
    fun accessToken(): String?          // 순수 토큰 (예: "eyJhbGciOi...")
    fun bearer(): String? = accessToken()?.let { "Bearer $it" }
}