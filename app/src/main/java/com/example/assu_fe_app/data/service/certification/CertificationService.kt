package com.example.assu_fe_app.data.service.certification

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.certification.request.PersonalCertificationRequestDto
import com.example.assu_fe_app.data.dto.certification.request.UserSessionRequestDto
import com.example.assu_fe_app.data.dto.certification.response.NoneDataResponseDto
import com.example.assu_fe_app.data.dto.certification.response.UserSessionResponseDto
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface CertificationService {

    @POST("/certification/session")
    suspend fun requestSessionId(
        request: UserSessionRequestDto)
    : BaseResponse<UserSessionResponseDto>

    @POST("/certification/personal")
    suspend fun postPersonalData(
        @Body request: PersonalCertificationRequestDto
    ) : BaseResponse<NoneDataResponseDto>
}