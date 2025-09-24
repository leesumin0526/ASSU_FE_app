package com.example.assu_fe_app.domain.usecase.auth

import com.example.assu_fe_app.data.dto.auth.PartnerSignUpRequestDto
import com.example.assu_fe_app.data.repository.auth.AuthRepository
import com.example.assu_fe_app.domain.model.auth.LoginModel
import com.example.assu_fe_app.util.RetrofitResult
import okhttp3.MultipartBody
import javax.inject.Inject

class PartnerSignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(request: PartnerSignUpRequestDto, licenseImage: MultipartBody.Part): RetrofitResult<LoginModel> {
        return authRepository.partnerSignUp(request, licenseImage)
    }
}
