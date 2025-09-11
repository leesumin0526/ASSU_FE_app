package com.example.assu_fe_app.domain.usecase.certification

import com.example.assu_fe_app.data.dto.certification.request.PersonalCertificationRequestDto
import com.example.assu_fe_app.data.repository.certification.CertificationRepository
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class PostPersonalDataUseCase @Inject constructor(
    private val repo: CertificationRepository
) {
    suspend operator fun invoke(
        request: PersonalCertificationRequestDto)
    : RetrofitResult<Void> {
        return  repo.postPersonalData(request)
    }

}