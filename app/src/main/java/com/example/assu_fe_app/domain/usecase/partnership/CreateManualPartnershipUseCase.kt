package com.example.assu_fe_app.domain.usecase.partnership

import com.example.assu_fe_app.data.dto.partnership.request.ContractImageParam
import com.example.assu_fe_app.data.dto.partnership.request.ManualPartnershipRequestDto
import com.example.assu_fe_app.data.repository.partnership.PartnershipRepository
import com.example.assu_fe_app.domain.model.partnership.ManualPartnershipModel
import com.example.assu_fe_app.util.RetrofitResult
import jakarta.inject.Inject

class CreateManualPartnershipUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(
        req: ManualPartnershipRequestDto,
        image: ContractImageParam?
    ): RetrofitResult<ManualPartnershipModel> = repo.createManualPartnership(req, image)
}