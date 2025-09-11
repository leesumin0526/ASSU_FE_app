package com.example.assu_fe_app.data.repository.certification

import com.example.assu_fe_app.data.dto.certification.request.PersonalCertificationRequestDto
import com.example.assu_fe_app.data.dto.certification.request.UserSessionRequestDto
import com.example.assu_fe_app.data.dto.certification.response.NoneDataResponseDto
import com.example.assu_fe_app.data.dto.certification.response.UserSessionResponseDto
import com.example.assu_fe_app.util.RetrofitResult
import okhttp3.ResponseBody

interface CertificationRepository {

    suspend fun requestSessionId(
        request: UserSessionRequestDto)
    : RetrofitResult<UserSessionResponseDto>

    suspend fun postPersonalData(
        request: PersonalCertificationRequestDto
    )
    : RetrofitResult<NoneDataResponseDto>

}