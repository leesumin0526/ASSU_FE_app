package com.assu.app.domain.usecase.partnership

import com.assu.app.data.repository.partnership.PartnershipRepository
import com.assu.app.domain.model.partnership.SuspendedPaperModel
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class GetSuspendedPapersUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(): RetrofitResult<List<SuspendedPaperModel>> =
        repo.getSuspendedPapers()
}