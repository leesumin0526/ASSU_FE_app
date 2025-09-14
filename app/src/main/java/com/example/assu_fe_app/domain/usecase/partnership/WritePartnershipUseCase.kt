package com.example.assu_fe_app.domain.usecase.partnership

import com.example.assu_fe_app.data.dto.partnership.request.WritePartnershipRequestDto
import com.example.assu_fe_app.data.repository.partnership.PartnershipRepository
import javax.inject.Inject

class WritePartnershipUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(request: WritePartnershipRequestDto) = repo.writePartnership(request)
}