package com.example.assu_fe_app.data.repositoryImpl.store

import com.example.assu_fe_app.data.dto.store.StorePartnershipResponseDto
import com.example.assu_fe_app.data.repository.review.ReviewRepository
import com.example.assu_fe_app.data.repository.store.StoreRepository
import com.example.assu_fe_app.data.service.review.ReviewService
import com.example.assu_fe_app.data.service.store.StoreService
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
import javax.inject.Inject

class StoreRepositoryImpl
@Inject constructor(
    private val api: StoreService
) : StoreRepository {
    override suspend fun getStorePartnership(storeId: Long)
    : RetrofitResult<StorePartnershipResponseDto> {
        return try{
            apiHandler(
                {api.getStorePartnership(storeId)},
                {serverResponse -> serverResponse}
            )
        }
        catch (e: Exception){
            RetrofitResult.Error(e)
        }
    }
}

