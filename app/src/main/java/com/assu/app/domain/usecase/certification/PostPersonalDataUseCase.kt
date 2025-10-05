package com.assu.app.domain.usecase.certification

import com.assu.app.data.dto.certification.request.PersonalCertificationRequestDto
import com.assu.app.data.dto.certification.response.NoneDataResponseDto
import com.assu.app.data.repository.certification.CertificationRepository
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class PostPersonalDataUseCase @Inject constructor(
    private val repo: CertificationRepository
) {
    suspend operator fun invoke(
        request: PersonalCertificationRequestDto)
    : RetrofitResult<NoneDataResponseDto> {
        return  repo.postPersonalData(request)
    }

}