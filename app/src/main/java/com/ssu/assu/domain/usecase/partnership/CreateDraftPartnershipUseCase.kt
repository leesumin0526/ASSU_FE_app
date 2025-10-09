package com.ssu.assu.domain.usecase.partnership

import com.ssu.assu.data.dto.partnership.request.CreateDraftRequestDto
import com.ssu.assu.data.repository.partnership.PartnershipRepository
import com.ssu.assu.domain.model.partnership.CreateDraftResponseModel
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class CreateDraftPartnershipUseCase @Inject constructor(private val repo: PartnershipRepository) {
    suspend operator fun invoke(request: CreateDraftRequestDto): RetrofitResult<CreateDraftResponseModel> {
        return repo.createDraftPartnership(request)
    }
}