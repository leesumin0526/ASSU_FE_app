package com.example.assu_fe_app.data.repositoryImpl.admin

import com.example.assu_fe_app.data.repository.admin.AdminHomeRepository
import com.example.assu_fe_app.data.service.admin.AdminHomeService
import com.example.assu_fe_app.domain.model.admin.RecommendedPartnerModel
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
import jakarta.inject.Inject

class AdminHomeRepositoryImpl @Inject constructor(
    private val api: AdminHomeService
) : AdminHomeRepository {

    override suspend fun getRecommendedPartner(): RetrofitResult<RecommendedPartnerModel> {
        return apiHandler(
            execute = { api.getRecommendedPartner() },
            mapper = { dto ->
                RecommendedPartnerModel(
                    partnerId = dto.partnerId,
                    partnerName = dto.partnerName,
                    partnerAddress = dto.partnerAddress,
                    partnerDetailAddress = dto.partnerDetailAddress,
                    partnerUrl = dto.partnerUrl
                )
            }
        )
    }
}