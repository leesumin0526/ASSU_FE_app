package com.assu.app.domain.usecase.partner

import com.assu.app.data.repository.partner.PartnerHomeRepository
import com.assu.app.domain.model.partner.RecommendedAdminModel
import com.assu.app.util.RetrofitResult
import jakarta.inject.Inject

class GetRecommendedAdminsUseCase @Inject constructor(
    private val repo: PartnerHomeRepository
) {
    suspend operator fun invoke(): RetrofitResult<List<RecommendedAdminModel>> {
        return repo.getRecommendedAdmins()
    }
}