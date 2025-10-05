package com.assu.app.domain.usecase.partnership

import com.assu.app.data.dto.partnership.request.CreateDraftRequestDto
import com.assu.app.data.repository.partnership.PartnershipRepository
import com.assu.app.domain.model.partnership.CreateDraftResponseModel
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class CreateDraftPartnershipUseCase @Inject constructor(private val repo: PartnershipRepository) {
    suspend operator fun invoke(request: CreateDraftRequestDto): RetrofitResult<CreateDraftResponseModel> {
        return repo.createDraftPartnership(request)
    }
}