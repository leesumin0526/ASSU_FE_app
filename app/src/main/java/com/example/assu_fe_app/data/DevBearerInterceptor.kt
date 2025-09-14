package com.example.assu_fe_app.data

import com.example.assu_fe_app.data.manager.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BearerInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = tokenManager.getAccessToken()
        val request = if (accessToken != null) {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}

