package com.assu.app.data.service.certification

import com.assu.app.data.dto.BaseResponse
import com.assu.app.data.dto.certification.request.PersonalCertificationRequestDto
import com.assu.app.data.dto.certification.request.UserSessionRequestDto
import com.assu.app.data.dto.certification.response.NoneDataResponseDto
import com.assu.app.data.dto.certification.response.UserSessionResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface CertificationService {

    @POST("/certification/session")
    suspend fun requestSessionId(
        @Body request: UserSessionRequestDto)
    : BaseResponse<UserSessionResponseDto>

    @POST("/certification/personal")
    suspend fun postPersonalData(
        @Body request: PersonalCertificationRequestDto
    ) : BaseResponse<NoneDataResponseDto>
}