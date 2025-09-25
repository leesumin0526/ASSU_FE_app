package com.example.assu_fe_app.domain.usecase.partnership

import com.example.assu_fe_app.data.repository.partnership.PartnershipRepository
import com.example.assu_fe_app.domain.model.partnership.UpdatePartnershipStatusResponseModel
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class UpdatePartnershipStatusUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(partnershipId: Long, status: String): RetrofitResult<UpdatePartnershipStatusResponseModel> {
        return repo.updatePartnershipStatus(partnershipId, status)
    }
}