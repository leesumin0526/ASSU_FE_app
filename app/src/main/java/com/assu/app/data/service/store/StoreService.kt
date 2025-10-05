package com.assu.app.data.service.store

import com.assu.app.data.dto.BaseResponse
import com.assu.app.data.dto.store.StorePartnershipResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface StoreService {

    @GET("store/{storeId}/papers")
    suspend fun getStorePartnership(
        @Path ("storeId") storeId: Long
    ) : BaseResponse<StorePartnershipResponseDto>
}