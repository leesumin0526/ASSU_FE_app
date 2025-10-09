package com.ssu.assu.domain.usecase.store

import com.ssu.assu.data.dto.store.StorePartnershipResponseDto
import com.ssu.assu.data.repository.store.StoreRepository
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class GetStorePartnershipUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    suspend operator fun invoke(storeId: Long)
    : RetrofitResult<StorePartnershipResponseDto>
    { return repository.getStorePartnership(storeId)}
}