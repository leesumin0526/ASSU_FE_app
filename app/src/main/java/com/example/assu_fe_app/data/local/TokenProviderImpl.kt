package com.example.assu_fe_app.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenProvider {

    override fun accessToken(): String? {
        // TODO: DataStore/SharedPreferences 등에서 실제 토큰 로드
        // 예시: SharedPreferences
        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        return prefs.getString("access_token", null)?.takeIf { it.isNotBlank() }
    }
}