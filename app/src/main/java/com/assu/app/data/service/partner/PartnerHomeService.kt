package com.assu.app.data.service.partner

import com.assu.app.data.dto.BaseResponse
import com.assu.app.data.dto.partner.RandomAdminResponseDto
import retrofit2.http.GET

interface PartnerHomeService {
    @GET("/partner/admin-recommend")
    suspend fun getRecommendedAdmins()
    : BaseResponse<RandomAdminResponseDto>
}