package com.ssu.assu.di

import com.ssu.assu.data.local.AccessTokenProvider
import com.ssu.assu.data.socket.ChatSocketClient
import com.ssu.assu.util.CertificationWebSocketClient
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
        // TODO: 실서버용
        val wsUrl = "wss://assu.shop/ws"
        // TODO:  로컬서버용
     // val wsUrl = "ws://10.0.2.2:8080/ws" // 서버 설정에 맞춰 변경
        return ChatSocketClient(wsUrl = wsUrl, accessTokenProvider = accessTokenProvider)
    }

    @Provides
    @Singleton
    fun provideCertificationClient(
        accessTokenProvider : AccessTokenProvider
    ) : CertificationWebSocketClient {
        val wsUrl = "wss://assu.shop/ws-certify"
        return CertificationWebSocketClient(wsUrl = wsUrl, tokenProvider = accessTokenProvider)
    }
}