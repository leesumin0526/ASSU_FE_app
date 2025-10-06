package com.ssu.assu.data.repository.dashboard

import com.ssu.assu.data.dto.dashboard.response.TodayBestResponseDto
import com.ssu.assu.data.dto.dashboard.response.WeeklyRankResponseDto
import com.ssu.assu.util.RetrofitResult

interface PartnerDashboardRepository {
    suspend fun getTodayBestStore(): RetrofitResult<TodayBestResponseDto>
    suspend fun getWeeklyRank(): RetrofitResult<WeeklyRankResponseDto>
    suspend fun getWeeklyRankList(): RetrofitResult<List<WeeklyRankResponseDto>>
}
