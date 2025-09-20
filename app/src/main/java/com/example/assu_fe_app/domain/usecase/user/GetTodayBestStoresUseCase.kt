package com.example.assu_fe_app.domain.usecase.user

import com.example.assu_fe_app.data.repository.user.UserHomeRepository
import com.example.assu_fe_app.domain.model.dashboard.PopularStoreModel
import com.example.assu_fe_app.util.RetrofitResult
import jakarta.inject.Inject

class GetTodayBestStoresUseCase @Inject constructor(
        private val repo: UserHomeRepository
    ) {
    suspend operator fun invoke(): RetrofitResult<List<PopularStoreModel>> {
        return repo.getTodayBestStores()
    }
}
