package com.example.assu_fe_app.domain.usecase.partnership

import com.example.assu_fe_app.data.repository.partnership.PartnershipRepository
import com.example.assu_fe_app.domain.model.partnership.SuspendedPaperModel
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class GetSuspendedPapersUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(): RetrofitResult<List<SuspendedPaperModel>> =
        repo.getSuspendedPapers()
}