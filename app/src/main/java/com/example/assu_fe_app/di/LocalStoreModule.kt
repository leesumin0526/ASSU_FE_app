package com.example.assu_fe_app.di

import android.content.Context
import com.example.assu_fe_app.data.local.DeviceTokenLocalStore
import com.example.assu_fe_app.data.local.DeviceTokenLocalStoreImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalStoreModule {

    @Provides
    @Singleton
    fun provideDeviceTokenLocalStore(
        @ApplicationContext context: Context
    ): DeviceTokenLocalStore = DeviceTokenLocalStoreImpl(context)
}
