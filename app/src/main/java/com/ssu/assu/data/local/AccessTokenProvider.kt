package com.ssu.assu.data.local

interface AccessTokenProvider {
    fun accessToken(): String?          // 순수 토큰 (예: "eyJhbGciOi...")
    fun bearer(): String? = accessToken()?.let { "Bearer $it" }
}