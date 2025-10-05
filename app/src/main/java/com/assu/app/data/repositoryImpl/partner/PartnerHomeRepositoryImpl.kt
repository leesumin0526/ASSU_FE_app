package com.assu.app.data.repositoryImpl.partner

import com.assu.app.data.repository.partner.PartnerHomeRepository
import com.assu.app.data.service.partner.PartnerHomeService
import com.assu.app.domain.model.partner.RecommendedAdminModel
import com.assu.app.util.RetrofitResult
import com.assu.app.util.apiHandler
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