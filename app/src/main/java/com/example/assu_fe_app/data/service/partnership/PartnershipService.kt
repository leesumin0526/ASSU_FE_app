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

interface PartnershipService {

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