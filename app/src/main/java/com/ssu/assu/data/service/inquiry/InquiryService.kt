package com.ssu.assu.data.service.inquiry

import com.ssu.assu.data.dto.BaseResponse
import com.ssu.assu.data.dto.inquiry.request.InquiryCreateRequestDto
import com.ssu.assu.data.dto.inquiry.response.InquiriesPageDto
import com.ssu.assu.data.dto.inquiry.response.InquiryResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface InquiryService {
    @POST("/member/inquiries")
    suspend fun create(
        @Body body: InquiryCreateRequestDto
    ): BaseResponse<Long>

    @GET("/member/inquiries")
    suspend fun list(
        @Query("status") status: String = "all",
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): BaseResponse<InquiriesPageDto>

    @GET("/member/inquiries/{inquiryId}")
    suspend fun get(
        @Path("inquiryId") id: Long
    ): BaseResponse<InquiryResponseDto>
}