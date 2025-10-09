package com.ssu.assu.domain.usecase.partnership

import com.ssu.assu.data.dto.partnership.request.WritePartnershipRequestDto
import com.ssu.assu.data.repository.partnership.PartnershipRepository
import com.ssu.assu.domain.model.partnership.WritePartnershipResponseModel
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class UpdatePartnershipUseCase @Inject constructor(private val repo: PartnershipRepository) {
    suspend operator fun invoke(request: WritePartnershipRequestDto): RetrofitResult<WritePartnershipResponseModel> {
        return repo.updatePartnership(request)
    }
}