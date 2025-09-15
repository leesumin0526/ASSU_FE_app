package com.example.assu_fe_app.data.repository.store

import com.example.assu_fe_app.data.dto.store.StorePartnershipResponseDto
import com.example.assu_fe_app.util.RetrofitResult

interface StoreRepository {
    suspend fun getStorePartnership(storeId: Long)
    : RetrofitResult<StorePartnershipResponseDto>


}