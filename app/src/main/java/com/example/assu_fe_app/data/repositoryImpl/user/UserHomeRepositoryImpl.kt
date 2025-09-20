package com.example.assu_fe_app.data.repositoryImpl.user

import com.example.assu_fe_app.data.repository.user.UserHomeRepository
import com.example.assu_fe_app.data.service.user.UserHomeService
import com.example.assu_fe_app.domain.model.dashboard.PopularStoreModel
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
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