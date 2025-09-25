package com.example.assu_fe_app.data.repositoryImpl.dashboard

import com.example.assu_fe_app.data.repository.dashboard.AdminDashboardRepository
import com.example.assu_fe_app.data.service.dashboard.AdminDashboardService
import com.example.assu_fe_app.domain.model.dashboard.AdminDashboardModel
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject
import com.example.assu_fe_app.util.apiHandler

class AdminDashboardRepositoryImpl @Inject constructor(
    private val api: AdminDashboardService
) : AdminDashboardRepository {

    override suspend fun getTotalStudentCount(): RetrofitResult<Long> {
        return apiHandler(
            execute = { api.getTotalStudentCount() },
            mapper = { dto -> dto.studentCount }
        )
    }

    override suspend fun getNewStudentCount(): RetrofitResult<Long> {
        return apiHandler(
            execute = { api.getNewStudentCount() },
            mapper = { dto -> dto.newStudentCount }
        )
    }

    override suspend fun getTodayUsageCount(): RetrofitResult<Long> {
        return apiHandler(
            execute = { api.getTodayUsageCount() },
            mapper = { dto -> dto.usagePersonCount }
        )
    }

    override suspend fun getMonthlyUsageCount(): RetrofitResult<Long> {
        return apiHandler(
            execute = { api.getMonthlyUsageCount() },
            mapper = { dto -> dto.monthlyUsageCount }
        )
    }

    override suspend fun getDetailedUsageList(): RetrofitResult<List<AdminDashboardModel.StoreUsageStat>> {
        return apiHandler(
            execute = { api.getDetailedUsageList() },
            mapper = { dto -> dto.items.map { it.toStoreUsageStat() } }
        )
    }
}
