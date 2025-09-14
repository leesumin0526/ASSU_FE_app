package com.example.assu_fe_app.di

import com.example.assu_fe_app.BuildConfig
import com.example.assu_fe_app.data.BearerInterceptor
import com.example.assu_fe_app.data.service.AuthService
import com.example.assu_fe_app.data.service.chatting.ChattingService
import com.example.assu_fe_app.data.service.dashboard.AdminDashboardService
import com.example.assu_fe_app.data.service.dashboard.PartnerDashboardService
import com.example.assu_fe_app.data.service.deviceToken.DeviceTokenService
import com.example.assu_fe_app.data.service.notification.NotificationService
import com.example.assu_fe_app.data.service.suggestion.SuggestionService
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
    fun provideOkHttp(bearerInterceptor: BearerInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(bearerInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // 개발에서만
            })
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

    @Provides @Singleton @NoAuth
    fun provideOkHttpNoAuth(): OkHttpClient =
        OkHttpClient.Builder()
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

    @Provides @Singleton @Auth
    fun provideRetrofit(@Auth client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides @Singleton @NoAuth
    fun provideRetrofitNoAuth(@NoAuth client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides @Singleton
    fun provideChattingService(@Auth retrofit: Retrofit): ChattingService =
        retrofit.create(ChattingService::class.java)

    @Provides @Singleton
    fun provideAuthService(@NoAuth retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideDeviceTokenService(@Auth retrofit: Retrofit): DeviceTokenService =
        retrofit.create(DeviceTokenService::class.java)

    @Provides
    @Singleton
    fun provideNotificationService(@Auth retrofit: Retrofit): NotificationService =
        retrofit.create(NotificationService::class.java)

    @Provides
    @Singleton
    fun provideSuggestionService(@Auth retrofit: Retrofit): SuggestionService =
        retrofit.create(SuggestionService::class.java)

    @Provides
    @Singleton
    fun provideAdminDashboardApiService(@Auth retrofit: Retrofit): AdminDashboardService =
        retrofit.create(AdminDashboardService::class.java)

    @Provides
    @Singleton
    fun providePartnerDashboardApiService(@Auth retrofit: Retrofit): PartnerDashboardService {
        return retrofit.create(PartnerDashboardService::class.java)
    }

}