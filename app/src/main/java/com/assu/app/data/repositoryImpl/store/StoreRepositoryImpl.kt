package com.assu.app.data.repositoryImpl.store

import com.assu.app.data.dto.store.StorePartnershipResponseDto
import com.assu.app.data.repository.store.StoreRepository
import com.assu.app.data.service.store.StoreService
import com.assu.app.util.RetrofitResult
import com.assu.app.util.apiHandler
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

