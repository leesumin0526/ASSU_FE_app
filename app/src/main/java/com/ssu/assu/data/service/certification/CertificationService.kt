package com.ssu.assu.data.service.certification

import com.ssu.assu.data.dto.BaseResponse
import com.ssu.assu.data.dto.certification.request.PersonalCertificationRequestDto
import com.ssu.assu.data.dto.certification.request.UserSessionRequestDto
import com.ssu.assu.data.dto.certification.response.NoneDataResponseDto
import com.ssu.assu.data.dto.certification.response.UserSessionResponseDto
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