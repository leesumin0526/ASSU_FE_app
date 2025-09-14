package com.example.assu_fe_app.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.assu_fe_app.BuildConfig
import com.example.assu_fe_app.data.BearerInterceptor
import com.example.assu_fe_app.data.service.AuthService
import com.example.assu_fe_app.data.service.certification.CertificationService
import com.example.assu_fe_app.data.service.chatting.ChattingService
import com.example.assu_fe_app.data.service.deviceToken.DeviceTokenService
import com.example.assu_fe_app.data.service.map.MapService
import com.example.assu_fe_app.data.service.notification.NotificationService
import com.example.assu_fe_app.data.service.review.ReviewService
import com.example.assu_fe_app.data.service.store.StoreService
import com.example.assu_fe_app.data.service.suggestion.SuggestionService
import com.example.assu_fe_app.data.service.usage.UsageService
import com.example.assu_fe_app.util.LocalDateMoshiAdapter
import com.google.gson.GsonBuilder
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
import java.time.LocalDate
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
    @RequiresApi(Build.VERSION_CODES.O)
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(LocalDate::class.java, LocalDateMoshiAdapter()) // LocalDate Adapter 추가
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

    @Provides @Singleton
    fun provideReviewService(@Auth retrofit: Retrofit): ReviewService =
        retrofit.create(ReviewService::class.java)

    @Provides @Singleton
    fun provideStoreService(@Auth retrofit: Retrofit): StoreService =
        retrofit.create(StoreService::class.java)

    @Provides @Singleton
    fun provideUsageService(@Auth retrofit: Retrofit): UsageService =
        retrofit.create(UsageService::class.java)

    @Provides @Singleton
    fun provideCertificationService(@Auth retrofit: Retrofit): CertificationService =
        retrofit.create(CertificationService::class.java)


    @Provides @Singleton
    fun provideMapService(@Auth retrofit: Retrofit): MapService =
        retrofit.create(MapService::class.java)
}