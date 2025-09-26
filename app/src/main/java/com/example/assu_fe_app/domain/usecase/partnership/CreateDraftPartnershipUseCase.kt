package com.example.assu_fe_app.domain.usecase.partnership

import com.example.assu_fe_app.data.dto.partnership.request.CreateDraftRequestDto
import com.example.assu_fe_app.data.repository.partnership.PartnershipRepository
import com.example.assu_fe_app.domain.model.partnership.CreateDraftResponseModel
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class CreateDraftPartnershipUseCase @Inject constructor(private val repo: PartnershipRepository) {
    suspend operator fun invoke(request: CreateDraftRequestDto): RetrofitResult<CreateDraftResponseModel> {
        return repo.createDraftPartnership(request)
    }
}