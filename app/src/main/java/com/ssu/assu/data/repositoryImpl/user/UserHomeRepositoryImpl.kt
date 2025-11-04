package com.ssu.assu.data.repositoryImpl.user

import com.ssu.assu.data.repository.user.UserHomeRepository
import com.ssu.assu.data.service.user.UserHomeService
import com.ssu.assu.domain.model.dashboard.PopularStoreModel
import com.ssu.assu.domain.model.user.GetUsablePartnershipModel
import com.ssu.assu.util.RetrofitResult
import com.ssu.assu.util.apiHandler
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

    override suspend fun getUsablePartnership(
        all: Boolean
    ): RetrofitResult<List<GetUsablePartnershipModel>> {
        return apiHandler(
            execute = { api.getUsablePartnership(all) },
            mapper = {dtoList -> dtoList.map { it.toModel()}}
        )
    }
}