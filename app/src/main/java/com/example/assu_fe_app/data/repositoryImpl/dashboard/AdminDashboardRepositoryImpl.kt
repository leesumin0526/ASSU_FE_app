package com.example.assu_fe_app.data.repositoryImpl.dashboard

import com.example.assu_fe_app.data.repository.dashboard.AdminDashboardRepository
import com.example.assu_fe_app.data.service.dashboard.AdminDashboardService
import com.example.assu_fe_app.domain.model.dashboard.StoreUsageModel
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
import javax.inject.Inject

class AdminDashboardRepositoryImpl @Inject constructor(
    private val api: AdminDashboardService
) : AdminDashboardRepository {

    override suspend fun getTotalStudentCount(): RetrofitResult<Long> {
        return apiHandler(
            { api.getTotalStudentCount() },
            { dto -> dto.studentCount }
        )
    }

    override suspend fun getNewStudentCount(): RetrofitResult<Long> {
        return apiHandler(
            { api.getNewStudentCount() },
            { dto -> dto.newStudentCount }
        )
    }

    override suspend fun getTodayUsageCount(): RetrofitResult<Long> {
        return apiHandler(
            { api.getTodayUsageCount() },
            { dto -> dto.usagePersonCount }
        )
    }

    override suspend fun getStoreUsageList(): RetrofitResult<List<StoreUsageModel>> {
        return apiHandler(
            { api.getStoreUsageList() },
            { dto ->
                dto.items.map { item ->
                    StoreUsageModel(
                        storeId = item.storeId,
                        storeName = item.storeName,
                        usageCount = item.usageCount
                    )
                }
            }
        )
    }
}