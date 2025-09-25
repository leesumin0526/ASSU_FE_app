package com.example.assu_fe_app.data.repository.dashboard

import com.example.assu_fe_app.domain.model.dashboard.AdminDashboardModel
import com.example.assu_fe_app.util.RetrofitResult

interface AdminDashboardRepository {
    suspend fun getTotalStudentCount(): RetrofitResult<Long>
    suspend fun getNewStudentCount(): RetrofitResult<Long>
    suspend fun getTodayUsageCount(): RetrofitResult<Long>
    suspend fun getMonthlyUsageCount(): RetrofitResult<Long>
    suspend fun getDetailedUsageList(): RetrofitResult<List<AdminDashboardModel.StoreUsageStat>>
}