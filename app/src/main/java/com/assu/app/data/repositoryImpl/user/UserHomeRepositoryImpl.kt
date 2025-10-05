package com.assu.app.data.repositoryImpl.user

import com.assu.app.data.repository.user.UserHomeRepository
import com.assu.app.data.service.user.UserHomeService
import com.assu.app.domain.model.dashboard.PopularStoreModel
import com.assu.app.util.RetrofitResult
import com.assu.app.util.apiHandler
import jakarta.inject.Inject

class UserHomeRepositoryImpl @Inject constructor(
    private val api: UserHomeService
) : UserHomeRepository {

    override suspend fun getStampCount(): RetrofitResult<Int> {
        return apiHandler(
            execute = { api.getStampCount() },
            mapper = { dto -> dto.stamp }
        )
    }
    override suspend fun getTodayBestStores(): RetrofitResult<List<PopularStoreModel>> {
        return apiHandler(
            execute = { api.getTodayBestStores() },
            mapper = { dto -> dto.toPopularStoreModels() }
        )
    }
}