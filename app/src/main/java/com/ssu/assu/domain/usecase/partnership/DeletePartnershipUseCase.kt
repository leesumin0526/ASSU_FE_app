package com.ssu.assu.domain.usecase.partnership

import com.ssu.assu.data.repository.partnership.PartnershipRepository
import com.ssu.assu.util.RetrofitResult
import jakarta.inject.Inject

class DeletePartnershipUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(paperId: Long): RetrofitResult<Unit> =
        repo.deletePartnership(paperId)
}