package com.example.assu_fe_app.domain.usecase.admin

import com.example.assu_fe_app.data.repository.admin.AdminHomeRepository
import com.example.assu_fe_app.domain.model.admin.RecommendedPartnerModel
import com.example.assu_fe_app.util.RetrofitResult
import jakarta.inject.Inject

class GetRecommendedPartnerUseCase @Inject constructor(
    private val repo: AdminHomeRepository
) {
    suspend operator fun invoke(): RetrofitResult<RecommendedPartnerModel> {
        return repo.getRecommendedPartner()
    }
}