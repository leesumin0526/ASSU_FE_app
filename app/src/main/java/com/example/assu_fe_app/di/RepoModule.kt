package com.example.assu_fe_app.di

import com.example.assu_fe_app.data.local.TokenProvider
import com.example.assu_fe_app.data.local.TokenProviderImpl
import com.example.assu_fe_app.data.remote.AuthInterceptor
import com.example.assu_fe_app.data.repository.auth.AuthRepository
import com.example.assu_fe_app.data.repository.chatting.ChattingRepository
import com.example.assu_fe_app.data.repositoryImpl.chatting.ChattingRepositoryImpl
import com.example.assu_fe_app.data.repository.deviceToken.DeviceTokenRepository
import com.example.assu_fe_app.data.repository.notification.NotificationRepository
import com.example.assu_fe_app.data.repository.partnership.PartnershipRepository
import com.example.assu_fe_app.data.repository.suggestion.SuggestionRepository
import com.example.assu_fe_app.data.repositoryImpl.AuthRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.deviceToken.DeviceTokenRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.notification.NotificationRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.partnership.PartnershipRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.suggestion.SuggestionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds @Singleton
    abstract fun bindChattingRepository(
        impl: ChattingRepositoryImpl
    ): ChattingRepository

    @Binds @Singleton
    abstract fun bindDeviceTokenRepository(
        impl: DeviceTokenRepositoryImpl
    ): DeviceTokenRepository

    @Binds
    @Singleton
    abstract fun bindTokenProvider(
        impl: TokenProviderImpl
    ): TokenProvider

    @Binds @Singleton
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl
    ): NotificationRepository

    @Binds @Singleton
    abstract fun bindSuggestionRepository(
        impl: SuggestionRepositoryImpl
    ): SuggestionRepository

    @Binds @Singleton
    abstract fun bindPartnershipRepository(
        impl: PartnershipRepositoryImpl
    ): PartnershipRepository
}