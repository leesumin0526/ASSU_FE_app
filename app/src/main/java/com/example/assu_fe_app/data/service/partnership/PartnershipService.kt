package com.example.assu_fe_app.data.service.partnership

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.partnership.response.GetProposalAdminListResponseDto
import com.example.assu_fe_app.data.dto.partnership.response.GetProposalPartnerListResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PartnershipService {
    // 관리자가 제휴업체 리스트 보는 api
    @GET("partnership/admin")
    suspend fun getProposalPartnerList(
        @Query("isAll") isAll: Boolean,
    ): BaseResponse<List<GetProposalPartnerListResponseDto>>

    @GET("partnership/partner")
    suspend fun getProposalAdminList (
        @Query("isAll") isAll: Boolean
    ): BaseResponse<List<GetProposalAdminListResponseDto>>
}