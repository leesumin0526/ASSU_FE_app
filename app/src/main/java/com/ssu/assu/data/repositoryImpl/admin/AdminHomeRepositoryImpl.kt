package com.ssu.assu.data.repositoryImpl.admin

import com.ssu.assu.data.repository.admin.AdminHomeRepository
import com.ssu.assu.data.service.admin.AdminHomeService
import com.ssu.assu.domain.model.admin.RecommendedPartnerModel
import com.ssu.assu.util.RetrofitResult
import com.ssu.assu.util.apiHandler
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