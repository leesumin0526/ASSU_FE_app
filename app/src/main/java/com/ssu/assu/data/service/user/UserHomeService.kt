package com.ssu.assu.data.service.user

import com.ssu.assu.data.dto.BaseResponse
import com.ssu.assu.data.dto.dashboard.response.TodayBestResponseDto
import com.ssu.assu.data.dto.user.home.StampResponseDto
import retrofit2.http.GET

interface UserHomeService {

    @GET("/students/stamp")
    suspend fun getStampCount(): BaseResponse<StampResponseDto>

    @GET("/store/best")
    suspend fun getTodayBestStores(): BaseResponse<TodayBestResponseDto>
}