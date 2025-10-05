package com.assu.app.domain.usecase.partnership

import com.assu.app.data.repository.partnership.PartnershipRepository
import com.assu.app.util.RetrofitResult
import jakarta.inject.Inject

class DeletePartnershipUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(paperId: Long): RetrofitResult<Unit> =
        repo.deletePartnership(paperId)
}