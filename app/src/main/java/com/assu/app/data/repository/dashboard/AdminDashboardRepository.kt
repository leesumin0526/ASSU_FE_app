package com.assu.app.data.repository.dashboard

import com.assu.app.domain.model.dashboard.AdminDashboardModel
import com.assu.app.util.RetrofitResult

interface AdminDashboardRepository {
    suspend fun getTotalStudentCount(): RetrofitResult<Long>
    suspend fun getNewStudentCount(): RetrofitResult<Long>
    suspend fun getTodayUsageCount(): RetrofitResult<Long>
    suspend fun getDetailedUsageList(): RetrofitResult<List<AdminDashboardModel.StoreUsageStat>>
}