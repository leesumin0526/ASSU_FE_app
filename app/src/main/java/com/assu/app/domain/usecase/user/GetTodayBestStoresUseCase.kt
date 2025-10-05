package com.assu.app.domain.usecase.user

import com.assu.app.data.repository.user.UserHomeRepository
import com.assu.app.domain.model.dashboard.PopularStoreModel
import com.assu.app.util.RetrofitResult
import jakarta.inject.Inject

class GetTodayBestStoresUseCase @Inject constructor(
        private val repo: UserHomeRepository
    ) {
    suspend operator fun invoke(): RetrofitResult<List<PopularStoreModel>> {
        return repo.getTodayBestStores()
    }
}
