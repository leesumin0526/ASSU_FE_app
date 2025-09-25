package com.example.assu_fe_app.domain.usecase.partner

import com.example.assu_fe_app.data.repository.partner.PartnerHomeRepository
import com.example.assu_fe_app.domain.model.partner.RecommendedAdminModel
import com.example.assu_fe_app.util.RetrofitResult
import jakarta.inject.Inject

class GetRecommendedAdminsUseCase @Inject constructor(
    private val repo: PartnerHomeRepository
) {
    suspend operator fun invoke(): RetrofitResult<List<RecommendedAdminModel>> {
        return repo.getRecommendedAdmins()
    }
}