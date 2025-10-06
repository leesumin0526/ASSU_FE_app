package com.ssu.assu.domain.usecase.partnership

import com.ssu.assu.data.repository.partnership.PartnershipRepository
import com.ssu.assu.domain.model.partnership.UpdatePartnershipStatusResponseModel
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class UpdatePartnershipStatusUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(partnershipId: Long, status: String): RetrofitResult<UpdatePartnershipStatusResponseModel> {
        return repo.updatePartnershipStatus(partnershipId, status)
    }
}