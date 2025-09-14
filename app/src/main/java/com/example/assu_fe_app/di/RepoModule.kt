package com.example.assu_fe_app.di

import com.example.assu_fe_app.data.repository.certification.CertificationRepository
import com.example.assu_fe_app.data.repository.chatting.ChattingRepository
import com.example.assu_fe_app.data.repositoryImpl.chatting.ChattingRepositoryImpl
import com.example.assu_fe_app.data.repository.deviceToken.DeviceTokenRepository
import com.example.assu_fe_app.data.repository.map.MapRepository
import com.example.assu_fe_app.data.repository.review.ReviewRepository
import com.example.assu_fe_app.data.repository.store.StoreRepository
import com.example.assu_fe_app.data.repository.usage.UsageRepository
import com.example.assu_fe_app.data.repositoryImpl.certification.CertificationRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.review.ReviewRepositoryImpl
import com.example.assu_fe_app.data.repository.notification.NotificationRepository
import com.example.assu_fe_app.data.repository.suggestion.SuggestionRepository
import com.example.assu_fe_app.data.repositoryImpl.deviceToken.DeviceTokenRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.map.MapRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.store.StoreRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.usage.UsageRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.notification.NotificationRepositoryImpl
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
    abstract fun bindChattingRepository(
        impl: ChattingRepositoryImpl
    ): ChattingRepository

    @Binds @Singleton
    abstract fun bindDeviceTokenRepository(
        impl: DeviceTokenRepositoryImpl
    ): DeviceTokenRepository

    @Binds @Singleton
    abstract fun bindReviewRepository(
        impl: ReviewRepositoryImpl
    ): ReviewRepository

    @Binds @Singleton
    abstract fun bindStoreRepository(
        impl: StoreRepositoryImpl
    ): StoreRepository

    @Binds @Singleton
    abstract fun bindUsageRepository(
        impl: UsageRepositoryImpl
    ): UsageRepository

    @Binds @Singleton
    abstract fun bindCertificationRepository(
        impl: CertificationRepositoryImpl
    ): CertificationRepository


    @Binds @Singleton
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl
    ): NotificationRepository

    @Binds @Singleton
    abstract fun bindSuggestionRepository(
        impl: SuggestionRepositoryImpl
    ): SuggestionRepository

    @Binds @Singleton
    abstract fun bindMapRepository(
        impl: MapRepositoryImpl
    ): MapRepository
}
