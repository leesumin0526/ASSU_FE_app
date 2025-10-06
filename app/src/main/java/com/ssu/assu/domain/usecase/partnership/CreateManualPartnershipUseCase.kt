package com.ssu.assu.domain.usecase.partnership

import com.ssu.assu.data.dto.partnership.request.ContractImageParam
import com.ssu.assu.data.dto.partnership.request.ManualPartnershipRequestDto
import com.ssu.assu.data.repository.partnership.PartnershipRepository
import com.ssu.assu.domain.model.partnership.ManualPartnershipModel
import com.ssu.assu.util.RetrofitResult
import jakarta.inject.Inject

class CreateManualPartnershipUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(
        req: ManualPartnershipRequestDto,
        image: ContractImageParam?
    ): RetrofitResult<ManualPartnershipModel> = repo.createManualPartnership(req, image)
}