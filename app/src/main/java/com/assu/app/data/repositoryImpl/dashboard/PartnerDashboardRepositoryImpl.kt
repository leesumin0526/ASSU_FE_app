package com.assu.app.data.repositoryImpl.dashboard


import com.assu.app.data.dto.dashboard.response.TodayBestResponseDto
import com.assu.app.data.dto.dashboard.response.WeeklyRankResponseDto
import com.assu.app.data.repository.dashboard.PartnerDashboardRepository
import com.assu.app.data.service.dashboard.PartnerDashboardService
import com.assu.app.util.RetrofitResult
import com.assu.app.util.apiHandler
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