package com.ssu.assu.di

import com.ssu.assu.data.repository.admin.AdminHomeRepository
import com.ssu.assu.data.repository.auth.AuthRepository
import com.ssu.assu.data.repository.certification.CertificationRepository
import com.ssu.assu.data.repository.chatting.ChattingRepository
import com.ssu.assu.data.repository.dashboard.AdminDashboardRepository
import com.ssu.assu.data.repository.dashboard.PartnerDashboardRepository
import com.ssu.assu.data.repositoryImpl.chatting.ChattingRepositoryImpl
import com.ssu.assu.data.repository.deviceToken.DeviceTokenRepository
import com.ssu.assu.data.repository.location.LocationRepository
import com.ssu.assu.data.repository.location.SearchRepository
import com.ssu.assu.data.repository.inquiry.InquiryRepository
import com.ssu.assu.data.repository.review.ReviewRepository
import com.ssu.assu.data.repository.store.StoreRepository
import com.ssu.assu.data.repository.usage.UsageRepository
import com.ssu.assu.data.repositoryImpl.certification.CertificationRepositoryImpl
import com.ssu.assu.data.repositoryImpl.review.ReviewRepositoryImpl
import com.ssu.assu.data.repository.notification.NotificationRepository
import com.ssu.assu.data.repository.partner.PartnerHomeRepository
import com.ssu.assu.data.repositoryImpl.dashboard.AdminDashboardRepositoryImpl
import com.ssu.assu.data.repositoryImpl.dashboard.PartnerDashboardRepositoryImpl
import com.ssu.assu.data.repository.partnership.PartnershipRepository
import com.ssu.assu.data.repository.profileImage.ProfileRepository
import com.ssu.assu.data.repository.report.ReportRepository
import com.ssu.assu.data.repository.suggestion.SuggestionRepository
import com.ssu.assu.data.repository.user.UserHomeRepository
import com.ssu.assu.data.repositoryImpl.AuthRepositoryImpl
import com.ssu.assu.data.repositoryImpl.admin.AdminHomeRepositoryImpl
import com.ssu.assu.data.repositoryImpl.deviceToken.DeviceTokenRepositoryImpl
import com.ssu.assu.data.repositoryImpl.location.LocationRepositoryImpl
import com.ssu.assu.data.repositoryImpl.location.SearchRepositoryImpl
import com.ssu.assu.data.repositoryImpl.inquiry.InquiryRepositoryImpl
import com.ssu.assu.data.repositoryImpl.store.StoreRepositoryImpl
import com.ssu.assu.data.repositoryImpl.usage.UsageRepositoryImpl
import com.ssu.assu.data.repositoryImpl.notification.NotificationRepositoryImpl
import com.ssu.assu.data.repositoryImpl.partner.PartnerHomeRepositoryImpl
import com.ssu.assu.data.repositoryImpl.partnership.PartnershipRepositoryImpl
import com.ssu.assu.data.repositoryImpl.profileImage.ProfileRepositoryImpl
import com.ssu.assu.data.repositoryImpl.report.ReportRepositoryImpl
import com.ssu.assu.data.repositoryImpl.suggestion.SuggestionRepositoryImpl
import com.ssu.assu.data.repositoryImpl.user.UserHomeRepositoryImpl
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