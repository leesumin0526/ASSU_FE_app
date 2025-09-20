package com.example.assu_fe_app.data.service.partner

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.partner.RandomAdminResponseDto
import retrofit2.http.GET

interface PartnerHomeService {
    @GET("/partner/admin-recommend")
    suspend fun getRecommendedAdmins()
    : BaseResponse<RandomAdminResponseDto>
}