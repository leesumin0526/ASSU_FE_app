package com.assu.app.data.service.user

import com.assu.app.data.dto.BaseResponse
import com.assu.app.data.dto.dashboard.response.TodayBestResponseDto
import com.assu.app.data.dto.user.home.StampResponseDto
import retrofit2.http.GET

interface UserHomeService {

    @GET("/students/stamp")
    suspend fun getStampCount(): BaseResponse<StampResponseDto>

    @GET("/store/best")
    suspend fun getTodayBestStores(): BaseResponse<TodayBestResponseDto>
}