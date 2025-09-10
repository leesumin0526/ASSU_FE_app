package com.example.assu_fe_app.data.service.usage

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.usage.response.UserMonthUsageResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface UsageService {

    @GET("/students/partnerships/{year}/{month}")
    suspend fun getMonthUsage(
        @Path("year") year: Int,
        @Path("month") month: Int
    ) : BaseResponse<UserMonthUsageResponseDto>
}