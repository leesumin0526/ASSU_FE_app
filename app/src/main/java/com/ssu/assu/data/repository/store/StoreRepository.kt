package com.ssu.assu.data.repository.store

import com.ssu.assu.data.dto.store.StorePartnershipResponseDto
import com.ssu.assu.util.RetrofitResult

interface StoreRepository {
    suspend fun getStorePartnership(storeId: Long)
    : RetrofitResult<StorePartnershipResponseDto>


}