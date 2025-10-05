package com.assu.app.data.service.usage

import com.assu.app.data.dto.BaseResponse
import com.assu.app.data.dto.certification.response.NoneDataResponseDto
import com.assu.app.data.dto.usage.SaveUsageRequestDto
import com.assu.app.data.dto.usage.response.GetUnreviewedUsageDto
import com.assu.app.data.dto.usage.response.UserMonthUsageResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

    @POST("/partnership/usage")
    suspend fun postUsage(
        @Body request: SaveUsageRequestDto
    ) : BaseResponse<NoneDataResponseDto>
}