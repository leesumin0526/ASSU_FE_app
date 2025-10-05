package com.assu.app.domain.usecase.dashboard

import com.assu.app.data.repository.dashboard.AdminDashboardRepository
import com.assu.app.domain.model.dashboard.AdminDashboardModel
import com.assu.app.util.RetrofitResult
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

class GetDetailedUsageListUseCase @Inject constructor(
    private val repo: AdminDashboardRepository
) {
    suspend operator fun invoke(): RetrofitResult<List<AdminDashboardModel.StoreUsageStat>> {
        return repo.getDetailedUsageList()
    }
}