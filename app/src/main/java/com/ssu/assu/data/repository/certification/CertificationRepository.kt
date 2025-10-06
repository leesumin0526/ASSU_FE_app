package com.ssu.assu.data.repository.certification

import com.ssu.assu.data.dto.certification.request.PersonalCertificationRequestDto
import com.ssu.assu.data.dto.certification.request.UserSessionRequestDto
import com.ssu.assu.data.dto.certification.response.NoneDataResponseDto
import com.ssu.assu.data.dto.certification.response.UserSessionResponseDto
import com.ssu.assu.util.RetrofitResult

interface CertificationRepository {

    suspend fun requestSessionId(
        request: UserSessionRequestDto)
    : RetrofitResult<UserSessionResponseDto>

    suspend fun postPersonalData(
        request: PersonalCertificationRequestDto
    )
    : RetrofitResult<NoneDataResponseDto>

}