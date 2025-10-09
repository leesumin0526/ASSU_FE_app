package com.ssu.assu.domain.usecase.partnership

import com.ssu.assu.data.repository.partnership.PartnershipRepository
import com.ssu.assu.domain.model.partnership.SuspendedPaperModel
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class GetSuspendedPapersUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(): RetrofitResult<List<SuspendedPaperModel>> =
        repo.getSuspendedPapers()
}