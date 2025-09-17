package com.example.assu_fe_app.data.service.dashboard

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.dashboard.response.TodayBestResponseDto
import com.example.assu_fe_app.data.dto.dashboard.response.WeeklyRankResponseDto
import retrofit2.http.GET

interface PartnerDashboardService {

    @GET("/store/best")
    suspend fun getTodayBestStore(): BaseResponse<TodayBestResponseDto>

    @GET("/store/ranking")
    suspend fun getWeeklyRank(): BaseResponse<WeeklyRankResponseDto>

    @GET("/store/ranking/weekly")
    suspend fun getWeeklyRankList(): BaseResponse<List<WeeklyRankResponseDto>>
}