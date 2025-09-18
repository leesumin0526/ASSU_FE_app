package com.example.assu_fe_app.data.repository.dashboard

import com.example.assu_fe_app.data.dto.dashboard.response.TodayBestResponseDto
import com.example.assu_fe_app.data.dto.dashboard.response.WeeklyRankResponseDto
import com.example.assu_fe_app.domain.model.dashboard.AdminDashboardModel
import com.example.assu_fe_app.domain.model.dashboard.PartnerDashboardModel
import com.example.assu_fe_app.domain.model.dashboard.PopularStoreModel
import com.example.assu_fe_app.domain.model.dashboard.WeeklyRankModel
import com.example.assu_fe_app.util.RetrofitResult

interface PartnerDashboardRepository {
    suspend fun getTodayBestStore(): RetrofitResult<TodayBestResponseDto>
    suspend fun getWeeklyRank(): RetrofitResult<WeeklyRankResponseDto>
    suspend fun getWeeklyRankList(): RetrofitResult<List<WeeklyRankResponseDto>>
}
