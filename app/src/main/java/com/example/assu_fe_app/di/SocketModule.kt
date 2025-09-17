package com.example.assu_fe_app.di

import com.example.assu_fe_app.data.local.AccessTokenProvider
import com.example.assu_fe_app.data.socket.ChatSocketClient
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
        val wsUrl = "wss://assu.shop/ws"
//        val wsUrl = "ws://10.0.2.2:8080/ws/chat-native" // 서버 설정에 맞춰 변경
        return ChatSocketClient(wsUrl = wsUrl, accessTokenProvider = accessTokenProvider)
//        val wsUrl = "ws://10.0.2.2:8080/ws/chat-native" // 서버 설정에 맞춰 변경
//        return ChatSocketClient(wsUrl = wsUrl, tokenProvider = tokenProvider)
    }
}