package com.ssu.assu.domain.usecase.certification

import com.ssu.assu.data.dto.certification.request.UserSessionRequestDto
import com.ssu.assu.data.dto.certification.response.UserSessionResponseDto
import com.ssu.assu.data.repository.certification.CertificationRepository
import com.ssu.assu.util.RetrofitResult
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