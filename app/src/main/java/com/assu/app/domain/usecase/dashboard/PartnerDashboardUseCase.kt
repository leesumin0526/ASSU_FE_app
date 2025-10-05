package com.assu.app.domain.usecase.dashboard


import com.assu.app.data.dto.dashboard.response.TodayBestResponseDto
import com.assu.app.data.dto.dashboard.response.WeeklyRankResponseDto
import com.assu.app.data.repository.dashboard.PartnerDashboardRepository
import com.assu.app.util.RetrofitResult
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