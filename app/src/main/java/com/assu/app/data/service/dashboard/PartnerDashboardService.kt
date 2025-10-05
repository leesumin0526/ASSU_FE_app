package com.assu.app.data.service.dashboard

import com.assu.app.data.dto.BaseResponse
import com.assu.app.data.dto.dashboard.response.TodayBestResponseDto
import com.assu.app.data.dto.dashboard.response.WeeklyRankResponseDto
import retrofit2.http.GET

interface PartnerDashboardService {

    @GET("/store/best")
    suspend fun getTodayBestStore(): BaseResponse<TodayBestResponseDto>

    @GET("/store/ranking")
    suspend fun getWeeklyRank(): BaseResponse<WeeklyRankResponseDto>

    @GET("/store/ranking/weekly")
    suspend fun getWeeklyRankList(): BaseResponse<List<WeeklyRankResponseDto>>
}