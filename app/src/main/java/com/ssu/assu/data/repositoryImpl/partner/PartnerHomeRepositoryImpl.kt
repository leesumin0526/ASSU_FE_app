package com.ssu.assu.data.repositoryImpl.partner

import com.ssu.assu.data.repository.partner.PartnerHomeRepository
import com.ssu.assu.data.service.partner.PartnerHomeService
import com.ssu.assu.domain.model.partner.RecommendedAdminModel
import com.ssu.assu.util.RetrofitResult
import com.ssu.assu.util.apiHandler
import jakarta.inject.Inject

class PartnerHomeRepositoryImpl @Inject constructor(
    private val api: PartnerHomeService
) : PartnerHomeRepository {

    override suspend fun getRecommendedAdmins(): RetrofitResult<List<RecommendedAdminModel>> {
        return apiHandler(
            execute = { api.getRecommendedAdmins() },
            mapper = { dto ->
                dto.admins.map { admin ->
                    RecommendedAdminModel(
                        adminId = admin.adminId,
                        adminName = admin.adminName,
                        adminAddress = admin.adminAddress,
                        adminDetailAddress = admin.adminDetailAddress,
                        adminUrl = admin.adminUrl,
                        adminPhone = admin.adminPhone
                    )
                }
            }
        )
    }
}