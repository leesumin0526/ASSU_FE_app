package com.example.assu_fe_app.domain.usecase.partnership

import com.example.assu_fe_app.data.repository.partnership.PartnershipRepository
import com.example.assu_fe_app.domain.model.partnership.ProposalPartnerDetailsModel
import com.example.assu_fe_app.util.RetrofitResult
import jakarta.inject.Inject

class GetPartnershipUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(partnershipId: Long)
            : RetrofitResult<ProposalPartnerDetailsModel> =
        repo.getPartnership(partnershipId)
}