package com.example.assu_fe_app.data.remote

import com.example.assu_fe_app.data.local.TokenProvider
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        tokenProvider.bearer()?.let { bearer ->
            // 이미 Authorization 헤더가 있으면 덮어쓰지 않도록 하고 싶다면 조건 추가
            if (original.header("Authorization").isNullOrBlank()) {
                builder.addHeader("Authorization", bearer)
            }
        }

        return chain.proceed(builder.build())
    }
}