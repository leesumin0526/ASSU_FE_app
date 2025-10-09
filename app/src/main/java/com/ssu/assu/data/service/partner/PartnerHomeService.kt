package com.ssu.assu.data.service.partner

import com.ssu.assu.data.dto.BaseResponse
import com.ssu.assu.data.dto.partner.RandomAdminResponseDto
import retrofit2.http.GET

interface PartnerHomeService {
    @GET("/partner/admin-recommend")
    suspend fun getRecommendedAdmins()
    : BaseResponse<RandomAdminResponseDto>
}