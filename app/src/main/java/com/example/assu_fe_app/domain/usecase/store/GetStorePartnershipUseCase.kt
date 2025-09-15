package com.example.assu_fe_app.domain.usecase.store

import com.example.assu_fe_app.data.dto.store.StorePartnershipResponseDto
import com.example.assu_fe_app.data.repository.store.StoreRepository
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class GetStorePartnershipUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    suspend operator fun invoke(storeId: Long)
    : RetrofitResult<StorePartnershipResponseDto>
    { return repository.getStorePartnership(storeId)}
}