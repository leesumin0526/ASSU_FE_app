package com.ssu.assu.data.repository.partner

import com.ssu.assu.domain.model.partner.RecommendedAdminModel
import com.ssu.assu.util.RetrofitResult

interface PartnerHomeRepository {
    suspend fun getRecommendedAdmins(): RetrofitResult<List<RecommendedAdminModel>>

}