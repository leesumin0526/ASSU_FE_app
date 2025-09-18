package com.example.assu_fe_app.data.local

import android.content.Context
import com.example.assu_fe_app.data.manager.TokenManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenProvider {
    lateinit var tokenManager : TokenManager

    override fun accessToken(): String? {
        tokenManager = TokenManager(context)
        // TODO: DataStore/SharedPreferences 등에서 실제 토큰 로드
        val token = tokenManager.getAccessToken()
        android.util.Log.d("WS", "TokenProviderImpl.accessToken()=$token")
        return token?.takeIf{ it.isNotBlank()}
    }
}