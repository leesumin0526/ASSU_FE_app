package com.ssu.assu.data.repository.dashboard

import com.ssu.assu.domain.model.dashboard.AdminDashboardModel
import com.ssu.assu.util.RetrofitResult

interface AdminDashboardRepository {
    suspend fun getTotalStudentCount(): RetrofitResult<Long>
    suspend fun getNewStudentCount(): RetrofitResult<Long>
    suspend fun getTodayUsageCount(): RetrofitResult<Long>
    suspend fun getDetailedUsageList(): RetrofitResult<List<AdminDashboardModel.StoreUsageStat>>
}