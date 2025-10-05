package com.assu.app.data.repository.store

import com.assu.app.data.dto.store.StorePartnershipResponseDto
import com.assu.app.util.RetrofitResult

interface StoreRepository {
    suspend fun getStorePartnership(storeId: Long)
    : RetrofitResult<StorePartnershipResponseDto>


}