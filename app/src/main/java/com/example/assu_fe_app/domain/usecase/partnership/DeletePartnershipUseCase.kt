package com.example.assu_fe_app.domain.usecase.partnership

import com.example.assu_fe_app.data.repository.partnership.PartnershipRepository
import com.example.assu_fe_app.util.RetrofitResult
import jakarta.inject.Inject

class DeletePartnershipUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(paperId: Long): RetrofitResult<Unit> =
        repo.deletePartnership(paperId)
}