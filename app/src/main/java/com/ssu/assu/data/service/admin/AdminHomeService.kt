package com.ssu.assu.data.service.admin

import com.ssu.assu.data.dto.BaseResponse
import com.ssu.assu.data.dto.admin.RandomPartnerResponseDto
import retrofit2.http.GET

interface AdminHomeService {
    @GET("/admin/partner-recommend")
    suspend fun getRecommendedPartner()
    : BaseResponse<RandomPartnerResponseDto>
}