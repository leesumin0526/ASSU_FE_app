package com.ssu.assu.domain.usecase.dashboard


import com.ssu.assu.data.dto.dashboard.response.TodayBestResponseDto
import com.ssu.assu.data.dto.dashboard.response.WeeklyRankResponseDto
import com.ssu.assu.data.repository.dashboard.PartnerDashboardRepository
import com.ssu.assu.util.RetrofitResult
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