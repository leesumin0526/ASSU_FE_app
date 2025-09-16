package com.example.assu_fe_app.domain.usecase.dashboard

import com.example.assu_fe_app.data.repository.dashboard.AdminDashboardRepository
import com.example.assu_fe_app.domain.model.dashboard.StoreUsageModel
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class GetTotalStudentCountUseCase @Inject constructor(
    private val repo: AdminDashboardRepository
) {
    suspend operator fun invoke(): RetrofitResult<Long> {
        return repo.getTotalStudentCount()
    }
}

class GetNewStudentCountUseCase @Inject constructor(
    private val repo: AdminDashboardRepository
) {
    suspend operator fun invoke(): RetrofitResult<Long> {
        return repo.getNewStudentCount()
    }
}

class GetTodayUsageCountUseCase @Inject constructor(
    private val repo: AdminDashboardRepository
) {
    suspend operator fun invoke(): RetrofitResult<Long> {
        return repo.getTodayUsageCount()
    }
}

class GetStoreUsageListUseCase @Inject constructor(
    private val repo: AdminDashboardRepository
) {
    suspend operator fun invoke(): RetrofitResult<List<StoreUsageModel>> {
        return repo.getStoreUsageList()
    }
}