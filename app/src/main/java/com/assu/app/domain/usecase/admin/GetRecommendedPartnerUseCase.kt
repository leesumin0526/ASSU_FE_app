package com.assu.app.domain.usecase.admin

import com.assu.app.data.repository.admin.AdminHomeRepository
import com.assu.app.domain.model.admin.RecommendedPartnerModel
import com.assu.app.util.RetrofitResult
import jakarta.inject.Inject

class GetRecommendedPartnerUseCase @Inject constructor(
    private val repo: AdminHomeRepository
) {
    suspend operator fun invoke(): RetrofitResult<RecommendedPartnerModel> {
        return repo.getRecommendedPartner()
    }
}