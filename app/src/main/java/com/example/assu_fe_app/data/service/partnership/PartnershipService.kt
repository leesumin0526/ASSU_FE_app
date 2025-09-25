package com.example.assu_fe_app.data.service.partnership

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.partnership.request.CreateDraftRequestDto
import com.example.assu_fe_app.data.dto.partnership.request.UpdatePartnershipStatusRequestDto
import com.example.assu_fe_app.data.dto.partnership.request.WritePartnershipRequestDto
import com.example.assu_fe_app.data.dto.partnership.response.AdminPartnershipStatusDto
import com.example.assu_fe_app.data.dto.partnership.response.CreateDraftResponseDto
import com.example.assu_fe_app.data.dto.partnership.response.PartnerPartnershipStatusDto
import com.example.assu_fe_app.data.dto.partnership.response.WritePartnershipResponseDto
import retrofit2.http.Body
import com.example.assu_fe_app.data.dto.partnership.response.GetProposalAdminListResponseDto
import com.example.assu_fe_app.data.dto.partnership.response.GetProposalPartnerListResponseDto
import com.example.assu_fe_app.data.dto.partnership.response.ManualPartnershipResponseDto
import com.example.assu_fe_app.data.dto.partnership.response.SuspendedPaperDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import com.example.assu_fe_app.data.dto.partnership.response.UpdatePartnershipStatusResponseDto
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PartnershipService {
    // 관리자가 제휴업체 리스트 보는 api
    @GET("partnership/admin")
    suspend fun getProposalPartnerList(
        @Query("isAll") isAll: Boolean,
    ): BaseResponse<List<GetProposalPartnerListResponseDto>>

    // 제휴업체가 관리자 리스트 보는 api
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

    // 제휴 제안서 상세 조회 API
    @GET("partnership/{partnershipId}")
    suspend fun getPartnership(
        @Path("partnershipId") partnershipId: Long
    ): BaseResponse<WritePartnershipResponseDto>

    // 제휴 상태 업데이트 API
    @PATCH("partnership/{partnershipId}/status")
    suspend fun updatePartnershipStatus(
        @Path("partnershipId") partnershipId: Long,
        @Body request: UpdatePartnershipStatusRequestDto
    ): BaseResponse<UpdatePartnershipStatusResponseDto>

    // 제휴 제안서 수동 등록 API
    @Multipart
    @POST("partnership/passivity")
    suspend fun createManualPartnership(
        @Part("request") requestJson: RequestBody,              // application/json
        @Part contractImage: MultipartBody.Part? = null         // image/*
    ): BaseResponse<ManualPartnershipResponseDto>

    // 대기 중인 제휴 계약서 조회 API
    @GET("partnership/suspended")
    suspend fun getSuspendedPapers(): BaseResponse<List<SuspendedPaperDto>>

    // 제휴 계약서 삭제 API
    @DELETE("partnership/proposal/delete/{paperId}")
    suspend fun deletePartnership(
        @Path("paperId") paperId: Long
    ): BaseResponse<Any>
}