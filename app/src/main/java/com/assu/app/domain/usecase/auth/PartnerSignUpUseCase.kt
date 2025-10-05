package com.assu.app.domain.usecase.auth

import com.assu.app.data.dto.auth.PartnerSignUpRequestDto
import com.assu.app.data.repository.auth.AuthRepository
import com.assu.app.domain.model.auth.LoginModel
import com.assu.app.util.RetrofitResult
import okhttp3.MultipartBody
import javax.inject.Inject

class PartnerSignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(request: PartnerSignUpRequestDto, licenseImage: MultipartBody.Part): RetrofitResult<LoginModel> {
        return authRepository.partnerSignUp(request, licenseImage)
    }
}
