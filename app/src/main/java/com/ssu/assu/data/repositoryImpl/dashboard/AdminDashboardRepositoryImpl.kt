package com.ssu.assu.data.repositoryImpl.dashboard

import com.ssu.assu.data.repository.dashboard.AdminDashboardRepository
import com.ssu.assu.data.service.dashboard.AdminDashboardService
import com.ssu.assu.domain.model.dashboard.AdminDashboardModel
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject
import com.ssu.assu.util.apiHandler

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

    override suspend fun getDetailedUsageList(): RetrofitResult<List<AdminDashboardModel.StoreUsageStat>> {
        return apiHandler(
            execute = { api.getDetailedUsageList() },
            mapper = { dto -> dto.items.map { it.toStoreUsageStat() } }
        )
    }
}
