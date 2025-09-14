package com.example.assu_fe_app.domain.usecase.dashboard


import com.example.assu_fe_app.data.dto.dashboard.response.TodayBestResponseDto
import com.example.assu_fe_app.data.dto.dashboard.response.WeeklyRankResponseDto
import com.example.assu_fe_app.data.repository.dashboard.PartnerDashboardRepository
import com.example.assu_fe_app.domain.model.dashboard.PartnerDashboardModel
import com.example.assu_fe_app.domain.model.dashboard.PopularStoreModel
import com.example.assu_fe_app.domain.model.dashboard.WeeklyRankModel
import com.example.assu_fe_app.util.RetrofitResult
import jakarta.inject.Inject


class GetTodayBestStoreUseCase @Inject constructor(
    private val repo: PartnerDashboardRepository
) {
    suspend operator fun invoke(): RetrofitResult<TodayBestResponseDto> {
        return repo.getTodayBestStore()
    }
}

class GetPartnerWeeklyRankUseCase @Inject constructor(
    private val repo: PartnerDashboardRepository
) {
    suspend operator fun invoke(): RetrofitResult<WeeklyRankResponseDto> {
        return repo.getWeeklyRank()
    }
}

class GetPartnerWeeklyRankListUseCase @Inject constructor(
    private val repo: PartnerDashboardRepository
) {
    suspend operator fun invoke(): RetrofitResult<List<WeeklyRankResponseDto>> {
        return repo.getWeeklyRankList()
    }
}