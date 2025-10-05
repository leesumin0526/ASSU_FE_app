package com.assu.app.data.service.admin

import com.assu.app.data.dto.BaseResponse
import com.assu.app.data.dto.admin.RandomPartnerResponseDto
import retrofit2.http.GET

interface AdminHomeService {
    @GET("/admin/partner-recommend")
    suspend fun getRecommendedPartner()
    : BaseResponse<RandomPartnerResponseDto>
}