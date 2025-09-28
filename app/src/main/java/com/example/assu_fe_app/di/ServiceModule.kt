package com.example.assu_fe_app.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.assu_fe_app.BuildConfig
import com.example.assu_fe_app.data.dto.converter.LocalDateAdapter
import com.example.assu_fe_app.data.dto.converter.LocalDateTimeAdapter
import com.example.assu_fe_app.data.remote.AuthInterceptor
import com.example.assu_fe_app.data.service.AuthService
import com.example.assu_fe_app.data.service.TokenRefreshAuthService
import com.example.assu_fe_app.data.service.admin.AdminHomeService
import com.example.assu_fe_app.data.service.NoAuthService
import com.example.assu_fe_app.data.service.certification.CertificationService
import com.example.assu_fe_app.data.service.chatting.ChattingService
import com.example.assu_fe_app.data.service.dashboard.AdminDashboardService
import com.example.assu_fe_app.data.service.dashboard.PartnerDashboardService
import com.example.assu_fe_app.data.service.deviceToken.DeviceTokenService
import com.example.assu_fe_app.data.service.inquiry.InquiryService
import com.example.assu_fe_app.data.service.location.LocationService
import com.example.assu_fe_app.data.service.location.SearchLocationService
import com.example.assu_fe_app.data.service.notification.NotificationService
import com.example.assu_fe_app.data.service.partner.PartnerHomeService
import com.example.assu_fe_app.data.service.partnership.PartnershipService
import com.example.assu_fe_app.data.service.profileService.ProfileService
import com.example.assu_fe_app.data.service.review.ReviewService
import com.example.assu_fe_app.data.service.store.StoreService
import com.example.assu_fe_app.data.service.suggestion.SuggestionService
import com.example.assu_fe_app.data.service.usage.UsageService
import com.example.assu_fe_app.data.service.user.UserHomeService
import com.example.assu_fe_app.util.LocalDateMoshiAdapter
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
import java.time.LocalDateTime
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

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class TokenRefresh

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides @Singleton @Auth
    fun provideOkHttp(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
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

    @Provides @Singleton @TokenRefresh
    fun provideOkHttpForTokenRefresh(): OkHttpClient =
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
            .add(LocalDateAdapter)
            .add(LocalDateTimeAdapter)
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

    @Provides @Singleton @TokenRefresh
    fun provideRetrofitForTokenRefresh(@TokenRefresh client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides @Singleton
    fun provideChattingService(@Auth retrofit: Retrofit): ChattingService =
        retrofit.create(ChattingService::class.java)

    @Provides @Singleton
    fun provideNoAuthService(@NoAuth retrofit: Retrofit): NoAuthService =
        retrofit.create(NoAuthService::class.java)

    @Provides @Singleton
    fun provideAuthService(@Auth retrofit: Retrofit): AuthService =
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
    fun provideLocationService(@Auth retrofit: Retrofit): LocationService =
        retrofit.create(LocationService::class.java)

    @Provides
    @Singleton
    fun provideAdminDashboardApiService(@Auth retrofit: Retrofit): AdminDashboardService =
        retrofit.create(AdminDashboardService::class.java)

    @Provides
    @Singleton
    fun providePartnerDashboardApiService(@Auth retrofit: Retrofit): PartnerDashboardService =
        retrofit.create(PartnerDashboardService::class.java)

    @Provides
    @Singleton
    fun provideUserHomeService(@Auth retrofit: Retrofit): UserHomeService =
        retrofit.create(UserHomeService::class.java)

    @Provides
    @Singleton
    fun providePartnershipService(@Auth retrofit: Retrofit): PartnershipService =
        retrofit.create(PartnershipService::class.java)

    @Provides
    @Singleton
    fun provideTokenRefreshAuthService(@TokenRefresh retrofit: Retrofit): TokenRefreshAuthService =
        retrofit.create(TokenRefreshAuthService::class.java)


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
    fun provideSearchService(@NoAuth retrofit: Retrofit) : SearchLocationService
    = retrofit.create(SearchLocationService::class.java)

    @Provides
    @Singleton
    fun provideInquiryService(@Auth retrofit: Retrofit): InquiryService =
        retrofit.create(InquiryService::class.java)

    @Provides
    @Singleton
    fun provideProfileService(@Auth retrofit: Retrofit): ProfileService =
        retrofit.create(ProfileService::class.java)

    @Provides
    @Singleton
    fun provideAdminHomeApiService(@Auth retrofit: Retrofit): AdminHomeService =
        retrofit.create(AdminHomeService::class.java)

    @Provides
    @Singleton
    fun providePartnerHomeService(@Auth retrofit: Retrofit): PartnerHomeService =
        retrofit.create(PartnerHomeService::class.java)


}