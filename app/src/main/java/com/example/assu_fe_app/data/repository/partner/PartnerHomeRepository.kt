package com.example.assu_fe_app.data.repository.partner

import com.example.assu_fe_app.domain.model.partner.RecommendedAdminModel
import com.example.assu_fe_app.util.RetrofitResult

interface PartnerHomeRepository {
    suspend fun getRecommendedAdmins(): RetrofitResult<List<RecommendedAdminModel>>

}