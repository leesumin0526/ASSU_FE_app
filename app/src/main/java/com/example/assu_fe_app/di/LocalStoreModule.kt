package com.example.assu_fe_app.di

import com.example.assu_fe_app.data.local.AccessTokenProvider
import com.example.assu_fe_app.data.local.AccessTokenProviderImpl
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import com.example.assu_fe_app.data.local.AuthTokenLocalStoreImpl
import com.example.assu_fe_app.data.local.DeviceTokenLocalStore
import com.example.assu_fe_app.data.local.DeviceTokenLocalStoreImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalStoreModule {

    @Binds
    @Singleton
    abstract fun bindAuthTokenLocalStore(
        authTokenLocalStoreImpl: AuthTokenLocalStoreImpl
    ): AuthTokenLocalStore

    @Binds
    @Singleton
    abstract fun bindAccessTokenProvider(
        accessTokenProviderImpl: AccessTokenProviderImpl
    ): AccessTokenProvider

    @Binds
    @Singleton
    abstract fun bindDeviceTokenLocalStore(
        deviceTokenLocalStoreImpl: DeviceTokenLocalStoreImpl
    ): DeviceTokenLocalStore
}