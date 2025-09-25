package com.example.assu_fe_app.data.repository.user

import com.example.assu_fe_app.domain.model.dashboard.PopularStoreModel
import com.example.assu_fe_app.util.RetrofitResult

interface UserHomeRepository {
    suspend fun getStampCount(): RetrofitResult<Int>
    suspend fun getTodayBestStores(): RetrofitResult<List<PopularStoreModel>>
}