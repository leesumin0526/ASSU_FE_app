package com.ssu.assu.domain.usecase.auth

import com.ssu.assu.data.dto.auth.AdminSignUpRequestDto
import com.ssu.assu.data.repository.auth.AuthRepository
import com.ssu.assu.domain.model.auth.LoginModel
import com.ssu.assu.util.RetrofitResult
import okhttp3.MultipartBody
import javax.inject.Inject

class AdminSignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(request: AdminSignUpRequestDto, signImage: MultipartBody.Part): RetrofitResult<LoginModel> {
        return authRepository.adminSignUp(request, signImage)
    }
}
