package com.assu.app.domain.usecase.partnership

import com.assu.app.data.dto.partnership.request.ContractImageParam
import com.assu.app.data.dto.partnership.request.ManualPartnershipRequestDto
import com.assu.app.data.repository.partnership.PartnershipRepository
import com.assu.app.domain.model.partnership.ManualPartnershipModel
import com.assu.app.util.RetrofitResult
import jakarta.inject.Inject

class CreateManualPartnershipUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(
        req: ManualPartnershipRequestDto,
        image: ContractImageParam?
    ): RetrofitResult<ManualPartnershipModel> = repo.createManualPartnership(req, image)
}