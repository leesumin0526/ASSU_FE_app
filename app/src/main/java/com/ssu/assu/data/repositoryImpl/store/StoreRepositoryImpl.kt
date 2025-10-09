package com.ssu.assu.data.repositoryImpl.store

import com.ssu.assu.data.dto.store.StorePartnershipResponseDto
import com.ssu.assu.data.repository.store.StoreRepository
import com.ssu.assu.data.service.store.StoreService
import com.ssu.assu.util.RetrofitResult
import com.ssu.assu.util.apiHandler
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

