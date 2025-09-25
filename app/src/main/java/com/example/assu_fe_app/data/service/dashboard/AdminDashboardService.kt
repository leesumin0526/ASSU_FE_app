package com.example.assu_fe_app.data.service.dashboard

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.dashboard.response.*
import retrofit2.http.GET

interface AdminDashboardService {

    @GET("/admin/dashBoard")
    suspend fun getTotalStudentCount(): BaseResponse<CountAdminAuthResponseDTO>

    @GET("/admin/dashBoard/new")
    suspend fun getNewStudentCount(): BaseResponse<NewCountAdminResponseDTO>

    @GET("/admin/dashBoard/countUser")
    suspend fun getTodayUsageCount(): BaseResponse<CountUsagePersonResponseDTO>

    @GET("/admin/dashBoard/usage/detailed")
    suspend fun getDetailedUsageList(): BaseResponse<CountUsageListResponseDTO>

    @GET("/admin/dashBoard/monthlyUsage")
    suspend fun getMonthlyUsageCount(): BaseResponse<MonthlyUsageCountResponseDTO>
}