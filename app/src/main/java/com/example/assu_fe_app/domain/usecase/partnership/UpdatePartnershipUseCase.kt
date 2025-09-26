package com.example.assu_fe_app.domain.usecase.partnership

import com.example.assu_fe_app.data.dto.partnership.request.WritePartnershipRequestDto
import com.example.assu_fe_app.data.repository.partnership.PartnershipRepository
import com.example.assu_fe_app.domain.model.partnership.WritePartnershipResponseModel
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class UpdatePartnershipUseCase @Inject constructor(private val repo: PartnershipRepository) {
    suspend operator fun invoke(request: WritePartnershipRequestDto): RetrofitResult<WritePartnershipResponseModel> {
        return repo.updatePartnership(request)
    }
}