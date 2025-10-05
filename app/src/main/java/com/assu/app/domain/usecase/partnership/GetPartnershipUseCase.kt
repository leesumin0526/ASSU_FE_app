package com.assu.app.domain.usecase.partnership

import com.assu.app.data.repository.partnership.PartnershipRepository
import com.assu.app.domain.model.partnership.ProposalPartnerDetailsModel
import com.assu.app.util.RetrofitResult
import jakarta.inject.Inject

class GetPartnershipUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(partnershipId: Long)
            : RetrofitResult<ProposalPartnerDetailsModel> =
        repo.getPartnership(partnershipId)
}