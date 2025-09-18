package com.example.assu_fe_app.di

import com.example.assu_fe_app.data.local.AccessTokenProvider
import com.example.assu_fe_app.data.socket.ChatSocketClient
import com.example.assu_fe_app.util.CertificationWebSocketClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object SocketModule {

    @Provides
    @Singleton
    fun provideChatSocketClient(
        accessTokenProvider: AccessTokenProvider // "Bearer xxx" 반환하도록 구현되어 있다고 가정
    ): ChatSocketClient {
        val wsUrl = "ws://10.0.2.2:8080/ws/chat-native" // 서버 설정에 맞춰 변경
        return ChatSocketClient(wsUrl = wsUrl, accessTokenProvider = accessTokenProvider)
    }

    @Provides
    @Singleton
    fun provideCertificationClient(
        tokenProvider : AccessTokenProvider
    ) : CertificationWebSocketClient {
        val wsUrl = "ws://10.0.2.2:8080/ws"
        return CertificationWebSocketClient(wsUrl = wsUrl, tokenProvider = tokenProvider)
    }
}