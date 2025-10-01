package com.example.assu_fe_app.di

import com.example.assu_fe_app.data.repository.admin.AdminHomeRepository
import com.example.assu_fe_app.data.repository.auth.AuthRepository
import com.example.assu_fe_app.data.repository.certification.CertificationRepository
import com.example.assu_fe_app.data.repository.chatting.ChattingRepository
import com.example.assu_fe_app.data.repository.dashboard.AdminDashboardRepository
import com.example.assu_fe_app.data.repository.dashboard.PartnerDashboardRepository
import com.example.assu_fe_app.data.repositoryImpl.chatting.ChattingRepositoryImpl
import com.example.assu_fe_app.data.repository.deviceToken.DeviceTokenRepository
import com.example.assu_fe_app.data.repository.location.LocationRepository
import com.example.assu_fe_app.data.repository.location.SearchRepository
import com.example.assu_fe_app.data.repository.inquiry.InquiryRepository
import com.example.assu_fe_app.data.repository.review.ReviewRepository
import com.example.assu_fe_app.data.repository.store.StoreRepository
import com.example.assu_fe_app.data.repository.usage.UsageRepository
import com.example.assu_fe_app.data.repositoryImpl.certification.CertificationRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.review.ReviewRepositoryImpl
import com.example.assu_fe_app.data.repository.notification.NotificationRepository
import com.example.assu_fe_app.data.repository.partner.PartnerHomeRepository
import com.example.assu_fe_app.data.repositoryImpl.dashboard.AdminDashboardRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.dashboard.PartnerDashboardRepositoryImpl
import com.example.assu_fe_app.data.repository.partnership.PartnershipRepository
import com.example.assu_fe_app.data.repository.profileImage.ProfileRepository
import com.example.assu_fe_app.data.repository.report.ReportRepository
import com.example.assu_fe_app.data.repository.suggestion.SuggestionRepository
import com.example.assu_fe_app.data.repository.user.UserHomeRepository
import com.example.assu_fe_app.data.repositoryImpl.AuthRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.admin.AdminHomeRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.deviceToken.DeviceTokenRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.location.LocationRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.location.SearchRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.inquiry.InquiryRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.store.StoreRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.usage.UsageRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.notification.NotificationRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.partner.PartnerHomeRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.partnership.PartnershipRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.profileImage.ProfileRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.report.ReportRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.suggestion.SuggestionRepositoryImpl
import com.example.assu_fe_app.data.repositoryImpl.user.UserHomeRepositoryImpl
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
    abstract fun bindAdminDashboardRepository(
        impl: AdminDashboardRepositoryImpl
    ): AdminDashboardRepository

    @Binds
    @Singleton
    abstract fun bindPartnerDashboardRepository(
        partnerDashboardRepositoryImpl: PartnerDashboardRepositoryImpl
    ): PartnerDashboardRepository

    @Binds @Singleton
    abstract fun bindInquiryRepository(
        impl: InquiryRepositoryImpl
    ): InquiryRepository

    @Binds @Singleton
    abstract fun bindSuggestionRepository(
        impl: SuggestionRepositoryImpl
    ): SuggestionRepository

    @Binds @Singleton
    abstract fun bindLocationRepository(
        impl: LocationRepositoryImpl
    ): LocationRepository

    @Binds @Singleton
    abstract fun bindPartnershipRepository(
        impl: PartnershipRepositoryImpl
    ): PartnershipRepository

    @Binds @Singleton
    abstract fun bindSearchRepository(
        impl: SearchRepositoryImpl
    ): SearchRepository

    @Binds
    @Singleton
    abstract fun bindUserHomeRepository(
        userHomeRepositoryImpl: UserHomeRepositoryImpl
    ): UserHomeRepository

    @Binds
    @Singleton
    abstract fun bindProfileImageRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindAdminHomeRepository(
        adminHomeRepositoryImpl: AdminHomeRepositoryImpl
    ): AdminHomeRepository

    @Binds
    @Singleton
    abstract fun bindPartnerHomeRepository(
        partnerHomeRepositoryImpl: PartnerHomeRepositoryImpl
    ): PartnerHomeRepository

    @Binds @Singleton
    abstract fun bindReportRepository(
        impl: ReportRepositoryImpl
    ): ReportRepository

}