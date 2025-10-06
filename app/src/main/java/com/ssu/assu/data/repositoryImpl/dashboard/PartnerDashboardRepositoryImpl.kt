package com.ssu.assu.data.repositoryImpl.dashboard


import com.ssu.assu.data.dto.dashboard.response.TodayBestResponseDto
import com.ssu.assu.data.dto.dashboard.response.WeeklyRankResponseDto
import com.ssu.assu.data.repository.dashboard.PartnerDashboardRepository
import com.ssu.assu.data.service.dashboard.PartnerDashboardService
import com.ssu.assu.util.RetrofitResult
import com.ssu.assu.util.apiHandler
import jakarta.inject.Inject

class PartnerDashboardRepositoryImpl @Inject constructor(
    private val api: PartnerDashboardService
) : PartnerDashboardRepository {

    override suspend fun getTodayBestStore(): RetrofitResult<TodayBestResponseDto> {
        return apiHandler(
            execute = { api.getTodayBestStore() },
            mapper = { dto -> dto }
        )
    }

    override suspend fun getWeeklyRank(): RetrofitResult<WeeklyRankResponseDto> {
        return apiHandler(
            execute = { api.getWeeklyRank() },
            mapper = { dto -> dto }
        )
    }

    override suspend fun getWeeklyRankList(): RetrofitResult<List<WeeklyRankResponseDto>> {
        return apiHandler(
            execute = { api.getWeeklyRankList() },
            mapper = { dto -> dto }
        )
    }
}