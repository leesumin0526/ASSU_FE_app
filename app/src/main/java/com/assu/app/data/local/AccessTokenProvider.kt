package com.assu.app.data.local

interface AccessTokenProvider {
    fun accessToken(): String?          // 순수 토큰 (예: "eyJhbGciOi...")
    fun bearer(): String? = accessToken()?.let { "Bearer $it" }
}