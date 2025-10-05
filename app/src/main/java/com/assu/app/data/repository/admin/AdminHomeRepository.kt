package com.assu.app.data.repository.admin

import com.assu.app.domain.model.admin.RecommendedPartnerModel
import com.assu.app.util.RetrofitResult

interface AdminHomeRepository {
    suspend fun getRecommendedPartner(): RetrofitResult<RecommendedPartnerModel>
}