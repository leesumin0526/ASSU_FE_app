package com.example.assu_fe_app.data.service.certification

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.certification.UserSessionRequestDto
import com.example.assu_fe_app.data.dto.certification.UserSessionResponseDto
import retrofit2.http.POST

interface CertificationService {

    @POST("/certification/session")
    suspend fun requestSessionId(
        request: UserSessionRequestDto)
    : BaseResponse<UserSessionResponseDto>
}