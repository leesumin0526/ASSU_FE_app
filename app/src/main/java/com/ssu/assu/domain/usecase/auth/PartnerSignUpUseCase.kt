package com.ssu.assu.domain.usecase.auth

import com.ssu.assu.data.dto.auth.PartnerSignUpRequestDto
import com.ssu.assu.data.repository.auth.AuthRepository
import com.ssu.assu.domain.model.auth.LoginModel
import com.ssu.assu.util.RetrofitResult
import okhttp3.MultipartBody
import javax.inject.Inject

class PartnerSignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(request: PartnerSignUpRequestDto, licenseImage: MultipartBody.Part): RetrofitResult<LoginModel> {
        return authRepository.partnerSignUp(request, licenseImage)
    }
}
