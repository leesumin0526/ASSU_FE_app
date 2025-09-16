package com.example.assu_fe_app.data.repository.admin

import com.example.assu_fe_app.domain.model.admin.RecommendedPartnerModel
import com.example.assu_fe_app.util.RetrofitResult

interface AdminHomeRepository {
    suspend fun getRecommendedPartner(): RetrofitResult<RecommendedPartnerModel>
}