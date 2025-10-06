package com.ssu.assu.domain.usecase.user

import com.ssu.assu.data.repository.user.UserHomeRepository
import com.ssu.assu.domain.model.dashboard.PopularStoreModel
import com.ssu.assu.util.RetrofitResult
import jakarta.inject.Inject

class GetTodayBestStoresUseCase @Inject constructor(
        private val repo: UserHomeRepository
    ) {
    suspend operator fun invoke(): RetrofitResult<List<PopularStoreModel>> {
        return repo.getTodayBestStores()
    }
}
