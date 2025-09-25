package com.example.assu_fe_app.data.service.partnership

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.partnership.response.GetProposalAdminListResponseDto
import com.example.assu_fe_app.data.dto.partnership.response.GetProposalPartnerListResponseDto
import com.example.assu_fe_app.data.dto.partnership.response.ManualPartnershipResponseDto
import com.example.assu_fe_app.data.dto.partnership.response.SuspendedPaperDto
import com.example.assu_fe_app.data.dto.partnership.response.WritePartnershipResponseDto
import com.squareup.moshi.JsonClass
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
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

    @GET("partnership/{partnershipId}")
    suspend fun getPartnership(
        @Path("partnershipId") partnershipId: Long
    ): BaseResponse<WritePartnershipResponseDto>

    @Multipart
    @POST("partnership/passivity")
    suspend fun createManualPartnership(
        @Part("request") requestJson: RequestBody,              // application/json
        @Part contractImage: MultipartBody.Part? = null         // image/*
    ): BaseResponse<ManualPartnershipResponseDto>

    @GET("partnership/suspended")
    suspend fun getSuspendedPapers(): BaseResponse<List<SuspendedPaperDto>>

    @DELETE("partnership/proposal/delete/{paperId}")
    suspend fun deletePartnership(
        @Path("paperId") paperId: Long
    ): BaseResponse<Any>
}