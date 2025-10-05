package com.assu.app.data.repository.dashboard

import com.assu.app.data.dto.dashboard.response.TodayBestResponseDto
import com.assu.app.data.dto.dashboard.response.WeeklyRankResponseDto
import com.assu.app.util.RetrofitResult

interface PartnerDashboardRepository {
    suspend fun getTodayBestStore(): RetrofitResult<TodayBestResponseDto>
    suspend fun getWeeklyRank(): RetrofitResult<WeeklyRankResponseDto>
    suspend fun getWeeklyRankList(): RetrofitResult<List<WeeklyRankResponseDto>>
}
