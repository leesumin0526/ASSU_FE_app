package com.assu.app.data.repository.partner

import com.assu.app.domain.model.partner.RecommendedAdminModel
import com.assu.app.util.RetrofitResult

interface PartnerHomeRepository {
    suspend fun getRecommendedAdmins(): RetrofitResult<List<RecommendedAdminModel>>

}