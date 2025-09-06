package com.example.assu_fe_app.data.service.store

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.store.StorePartnershipResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface StoreService {

    @GET("store/{storeId}/papers")
    suspend fun getStorePartnership(
        @Path ("storeId") storeId: Long
    ) : BaseResponse<StorePartnershipResponseDto>
}