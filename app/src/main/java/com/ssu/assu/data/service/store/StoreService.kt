package com.ssu.assu.data.service.store

import com.ssu.assu.data.dto.BaseResponse
import com.ssu.assu.data.dto.store.StorePartnershipResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface StoreService {

    @GET("store/{storeId}/papers")
    suspend fun getStorePartnership(
        @Path ("storeId") storeId: Long
    ) : BaseResponse<StorePartnershipResponseDto>
}