package com.example.assu_fe_app.domain.usecase.certification

import com.example.assu_fe_app.data.dto.certification.UserSessionRequestDto
import com.example.assu_fe_app.data.dto.certification.UserSessionResponseDto
import com.example.assu_fe_app.data.dto.review.response.ReviewAverageResponseDto
import com.example.assu_fe_app.data.repository.certification.CertificationRepository
import com.example.assu_fe_app.util.RetrofitResult
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