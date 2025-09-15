package com.example.assu_fe_app.data.service.partnership

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.partnership.request.CreateDraftRequestDto
import com.example.assu_fe_app.data.dto.partnership.request.WritePartnershipRequestDto
import com.example.assu_fe_app.data.dto.partnership.response.AdminPartnershipStatusDto
import com.example.assu_fe_app.data.dto.partnership.response.CreateDraftResponseDto
import com.example.assu_fe_app.data.dto.partnership.response.PartnerPartnershipStatusDto
import com.example.assu_fe_app.data.dto.partnership.response.WritePartnershipResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query
import com.example.assu_fe_app.data.dto.partnership.response.GetProposalAdminListResponseDto
import com.example.assu_fe_app.data.dto.partnership.response.GetProposalPartnerListResponseDto

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

    //제휴 제안서 초안 작성 API
    @POST("partnership/proposal/draft")
    suspend fun createDraftPartnership(
        @Body request: CreateDraftRequestDto
    ): BaseResponse<CreateDraftResponseDto>

    //제휴 제안서 업데이트 API
    @PATCH("partnership/proposal")
    suspend fun updatePartnership(
        @Body request: WritePartnershipRequestDto
    ): BaseResponse<WritePartnershipResponseDto>

    // 채팅방 내 제휴 상태 확인 API
    @GET("partnership/check/admin")
    suspend fun checkPartnershipAsAdmin(
        @Query("partnerId") partnerId: Long
    ): BaseResponse<AdminPartnershipStatusDto>
    @GET("partnership/check/partner")
    suspend fun checkPartnershipAsPartner(
        @Query("adminId") adminId: Long
    ): BaseResponse<PartnerPartnershipStatusDto>


}