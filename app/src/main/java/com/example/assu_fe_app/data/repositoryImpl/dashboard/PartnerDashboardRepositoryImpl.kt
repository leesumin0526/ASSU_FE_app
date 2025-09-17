package com.example.assu_fe_app.data.repositoryImpl.dashboard


import com.example.assu_fe_app.data.dto.dashboard.response.TodayBestResponseDto
import com.example.assu_fe_app.data.dto.dashboard.response.WeeklyRankResponseDto
import com.example.assu_fe_app.data.repository.dashboard.PartnerDashboardRepository
import com.example.assu_fe_app.data.service.dashboard.PartnerDashboardService
import com.example.assu_fe_app.domain.model.dashboard.PartnerDashboardModel
import com.example.assu_fe_app.domain.model.dashboard.PopularStoreModel
import com.example.assu_fe_app.domain.model.dashboard.StoreInfoModel
import com.example.assu_fe_app.domain.model.dashboard.WeeklyRankModel
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
import jakarta.inject.Inject
import kotlinx.coroutines.async

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