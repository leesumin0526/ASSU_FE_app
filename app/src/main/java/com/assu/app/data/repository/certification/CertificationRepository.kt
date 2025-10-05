package com.assu.app.data.repository.certification

import com.assu.app.data.dto.certification.request.PersonalCertificationRequestDto
import com.assu.app.data.dto.certification.request.UserSessionRequestDto
import com.assu.app.data.dto.certification.response.NoneDataResponseDto
import com.assu.app.data.dto.certification.response.UserSessionResponseDto
import com.assu.app.util.RetrofitResult

interface CertificationRepository {

    suspend fun requestSessionId(
        request: UserSessionRequestDto)
    : RetrofitResult<UserSessionResponseDto>

    suspend fun postPersonalData(
        request: PersonalCertificationRequestDto
    )
    : RetrofitResult<NoneDataResponseDto>

}