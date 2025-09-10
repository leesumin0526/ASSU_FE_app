package com.example.assu_fe_app.di

import com.example.assu_fe_app.BuildConfig
import com.example.assu_fe_app.data.DevBearerInterceptor
import com.example.assu_fe_app.data.service.chatting.ChattingService
import com.example.assu_fe_app.data.service.deviceToken.DeviceTokenService
import com.example.assu_fe_app.data.service.notification.NotificationService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    const val NETWORK_EXCEPTION_OFFLINE_CASE = "network status is offline"
    const val NETWORK_EXCEPTION_BODY_IS_NULL = "result body is null"

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Auth

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class NoAuth

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class S3

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides @Singleton @Auth
    fun provideOkHttp(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(DevBearerInterceptor()) // 🔴 임시
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // 개발에서만
            })
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

    @Provides @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())  // ← 추가
            .build()

    @Provides @Singleton
    fun provideRetrofit(@Auth client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides @Singleton
    fun provideChattingService(retrofit: Retrofit): ChattingService =
        retrofit.create(ChattingService::class.java)

    // 🔴 임시
    @Provides
    @Singleton
    fun provideDeviceTokenService(retrofit: Retrofit): DeviceTokenService =
        retrofit.create(DeviceTokenService::class.java)

    @Provides
    @Singleton
    fun provideNotificationService(retrofit: Retrofit): NotificationService =
        retrofit.create(NotificationService::class.java)
}