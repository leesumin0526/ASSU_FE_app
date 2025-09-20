package com.example.assu_fe_app.data.repositoryImpl.partner

import com.example.assu_fe_app.data.repository.partner.PartnerHomeRepository
import com.example.assu_fe_app.data.service.partner.PartnerHomeService
import com.example.assu_fe_app.domain.model.partner.RecommendedAdminModel
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
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
                    )
                }
            }
        )
    }
}