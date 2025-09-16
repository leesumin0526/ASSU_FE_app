package com.example.assu_fe_app.data.service.dashboard

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.dashboard.response.AdminDashboardResponseDto
import com.example.assu_fe_app.data.dto.dashboard.response.CountAdminAuthResponseDTO
import com.example.assu_fe_app.data.dto.dashboard.response.CountUsageListResponseDTO
import com.example.assu_fe_app.data.dto.dashboard.response.CountUsagePersonResponseDTO
import com.example.assu_fe_app.data.dto.dashboard.response.NewCountAdminResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface AdminDashboardService {

    @GET("/admin/dashBoard")
    suspend fun getTotalStudentCount()
    : BaseResponse<CountAdminAuthResponseDTO>

    @GET("/admin/dashBoard/new")
    suspend fun getNewStudentCount()
    : BaseResponse<NewCountAdminResponseDTO>

    @GET("/admin/dashBoard/countUser")
    suspend fun getTodayUsageCount()
    : BaseResponse<CountUsagePersonResponseDTO>

    @GET("/admin/dashBoard/usage")
    suspend fun getStoreUsageList()
    : BaseResponse<CountUsageListResponseDTO>
}