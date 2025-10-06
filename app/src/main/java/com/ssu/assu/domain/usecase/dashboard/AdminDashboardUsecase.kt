package com.ssu.assu.domain.usecase.dashboard

import com.ssu.assu.data.repository.dashboard.AdminDashboardRepository
import com.ssu.assu.domain.model.dashboard.AdminDashboardModel
import com.ssu.assu.util.RetrofitResult
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