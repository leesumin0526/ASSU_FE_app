package com.ssu.assu.domain.usecase.admin

import com.ssu.assu.data.repository.admin.AdminHomeRepository
import com.ssu.assu.domain.model.admin.RecommendedPartnerModel
import com.ssu.assu.util.RetrofitResult
import jakarta.inject.Inject

class GetRecommendedPartnerUseCase @Inject constructor(
    private val repo: AdminHomeRepository
) {
    suspend operator fun invoke(): RetrofitResult<RecommendedPartnerModel> {
        return repo.getRecommendedPartner()
    }
}