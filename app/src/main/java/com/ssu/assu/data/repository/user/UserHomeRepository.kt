package com.ssu.assu.data.repository.user

import com.ssu.assu.domain.model.dashboard.PopularStoreModel
import com.ssu.assu.domain.model.user.GetUsablePartnershipModel
import com.ssu.assu.util.RetrofitResult

interface UserHomeRepository {
    suspend fun getStampCount(): RetrofitResult<Int>
    suspend fun getTodayBestStores(): RetrofitResult<List<PopularStoreModel>>
    suspend fun getUsablePartnership(all: Boolean): RetrofitResult<List<GetUsablePartnershipModel>>
}