package com.example.assu_fe_app.data.repository.certification

import com.example.assu_fe_app.data.dto.certification.UserSessionRequestDto
import com.example.assu_fe_app.data.dto.certification.UserSessionResponseDto
import com.example.assu_fe_app.util.RetrofitResult

interface CertificationRepository {

    suspend fun requestSessionId(
        request: UserSessionRequestDto)
    : RetrofitResult<UserSessionResponseDto>

}