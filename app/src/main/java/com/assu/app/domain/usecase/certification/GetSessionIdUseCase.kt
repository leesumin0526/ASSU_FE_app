package com.assu.app.domain.usecase.certification

import com.assu.app.data.dto.certification.request.UserSessionRequestDto
import com.assu.app.data.dto.certification.response.UserSessionResponseDto
import com.assu.app.data.repository.certification.CertificationRepository
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class GetSessionIdUseCase @Inject constructor(
    private val repo: CertificationRepository
) {
    suspend operator fun invoke(
        request: UserSessionRequestDto
    ): RetrofitResult<UserSessionResponseDto> {
        return repo.requestSessionId(request)
    }
}