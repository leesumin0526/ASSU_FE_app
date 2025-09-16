package com.example.assu_fe_app.data.service.user

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.dashboard.response.TodayBestResponseDto
import com.example.assu_fe_app.data.dto.user.home.StampResponseDto
import retrofit2.http.GET

interface UserHomeService {

    @GET("/students/stamp")
    suspend fun getStampCount(): BaseResponse<StampResponseDto>

    @GET("/store/best")
    suspend fun getTodayBestStores(): BaseResponse<TodayBestResponseDto>
}