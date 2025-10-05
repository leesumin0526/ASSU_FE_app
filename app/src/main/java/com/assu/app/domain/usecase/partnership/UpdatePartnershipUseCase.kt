package com.assu.app.domain.usecase.partnership

import com.assu.app.data.dto.partnership.request.WritePartnershipRequestDto
import com.assu.app.data.repository.partnership.PartnershipRepository
import com.assu.app.domain.model.partnership.WritePartnershipResponseModel
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class UpdatePartnershipUseCase @Inject constructor(private val repo: PartnershipRepository) {
    suspend operator fun invoke(request: WritePartnershipRequestDto): RetrofitResult<WritePartnershipResponseModel> {
        return repo.updatePartnership(request)
    }
}