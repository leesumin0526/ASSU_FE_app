package com.ssu.assu.di

import com.ssu.assu.data.local.AccessTokenProvider
import com.ssu.assu.data.local.AccessTokenProviderImpl
import com.ssu.assu.data.local.AuthTokenLocalStore
import com.ssu.assu.data.local.AuthTokenLocalStoreImpl
import com.ssu.assu.data.local.DeviceTokenLocalStore
import com.ssu.assu.data.local.DeviceTokenLocalStoreImpl
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