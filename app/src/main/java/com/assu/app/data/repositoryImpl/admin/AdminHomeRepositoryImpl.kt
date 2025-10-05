package com.assu.app.data.repositoryImpl.admin

import com.assu.app.data.repository.admin.AdminHomeRepository
import com.assu.app.data.service.admin.AdminHomeService
import com.assu.app.domain.model.admin.RecommendedPartnerModel
import com.assu.app.util.RetrofitResult
import com.assu.app.util.apiHandler
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
                    partnerUrl = dto.partnerUrl,
                    partnerPhone = dto.partnerPhone
                )
            }
        )
    }
}