package com.ssu.assu.domain.usecase.partner

import com.ssu.assu.data.repository.partner.PartnerHomeRepository
import com.ssu.assu.domain.model.partner.RecommendedAdminModel
import com.ssu.assu.util.RetrofitResult
import jakarta.inject.Inject

class GetRecommendedAdminsUseCase @Inject constructor(
    private val repo: PartnerHomeRepository
) {
    suspend operator fun invoke(): RetrofitResult<List<RecommendedAdminModel>> {
        return repo.getRecommendedAdmins()
    }
}