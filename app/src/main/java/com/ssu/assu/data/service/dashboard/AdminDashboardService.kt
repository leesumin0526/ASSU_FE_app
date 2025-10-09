package com.ssu.assu.data.service.dashboard

import com.ssu.assu.data.dto.BaseResponse
import com.ssu.assu.data.dto.dashboard.response.*
import retrofit2.http.GET

interface AdminDashboardService {

    @GET("/admin/dashBoard")
    suspend fun getTotalStudentCount(): BaseResponse<CountAdminAuthResponseDTO>

    @GET("/admin/dashBoard/new")
    suspend fun getNewStudentCount(): BaseResponse<NewCountAdminResponseDTO>

    @GET("/admin/dashBoard/countUser")
    suspend fun getTodayUsageCount(): BaseResponse<CountUsagePersonResponseDTO>

    @GET("/admin/dashBoard/usage")
    suspend fun getDetailedUsageList(): BaseResponse<CountUsageListResponseDTO>

}