package com.example.assu_fe_app.data.service.usage

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.usage.GetUnreviewedUsageDto
import com.example.assu_fe_app.data.dto.usage.response.UserMonthUsageResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UsageService {

    @GET("/students/partnerships/{year}/{month}")
    suspend fun getMonthUsage(
        @Path("year") year: Int,
        @Path("month") month: Int
    ) : BaseResponse<UserMonthUsageResponseDto>

    @GET("/students/usage")
    suspend fun getUnreviewedUsage(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String
    ) : BaseResponse<GetUnreviewedUsageDto>
}