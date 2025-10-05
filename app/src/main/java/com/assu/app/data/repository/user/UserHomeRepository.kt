package com.assu.app.data.repository.user

import com.assu.app.domain.model.dashboard.PopularStoreModel
import com.assu.app.util.RetrofitResult

interface UserHomeRepository {
    suspend fun getStampCount(): RetrofitResult<Int>
    suspend fun getTodayBestStores(): RetrofitResult<List<PopularStoreModel>>
}