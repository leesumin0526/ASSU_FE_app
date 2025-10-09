package com.ssu.assu.data.repository.admin

import com.ssu.assu.domain.model.admin.RecommendedPartnerModel
import com.ssu.assu.util.RetrofitResult

interface AdminHomeRepository {
    suspend fun getRecommendedPartner(): RetrofitResult<RecommendedPartnerModel>
}