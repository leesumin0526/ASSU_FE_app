package com.example.assu_fe_app.data.service.admin

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.admin.RandomPartnerResponseDto
import retrofit2.http.GET

interface AdminHomeService {
    @GET("/admin/partner-recommend")
    suspend fun getRecommendedPartner(): BaseResponse<RandomPartnerResponseDto>
}