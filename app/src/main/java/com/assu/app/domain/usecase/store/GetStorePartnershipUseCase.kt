package com.assu.app.domain.usecase.store

import com.assu.app.data.dto.store.StorePartnershipResponseDto
import com.assu.app.data.repository.store.StoreRepository
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class GetStorePartnershipUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    suspend operator fun invoke(storeId: Long)
    : RetrofitResult<StorePartnershipResponseDto>
    { return repository.getStorePartnership(storeId)}
}