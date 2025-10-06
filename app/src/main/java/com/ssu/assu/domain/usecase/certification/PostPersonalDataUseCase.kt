package com.ssu.assu.domain.usecase.certification

import com.ssu.assu.data.dto.certification.request.PersonalCertificationRequestDto
import com.ssu.assu.data.dto.certification.response.NoneDataResponseDto
import com.ssu.assu.data.repository.certification.CertificationRepository
import com.ssu.assu.util.RetrofitResult
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