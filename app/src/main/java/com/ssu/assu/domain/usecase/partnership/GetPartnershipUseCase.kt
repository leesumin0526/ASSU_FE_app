package com.ssu.assu.domain.usecase.partnership

import com.ssu.assu.data.repository.partnership.PartnershipRepository
import com.ssu.assu.domain.model.partnership.ProposalPartnerDetailsModel
import com.ssu.assu.util.RetrofitResult
import jakarta.inject.Inject

class GetPartnershipUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(partnershipId: Long)
            : RetrofitResult<ProposalPartnerDetailsModel> =
        repo.getPartnership(partnershipId)
}