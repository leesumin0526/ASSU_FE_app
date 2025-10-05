package com.assu.app.domain.usecase.auth

import com.assu.app.data.dto.auth.AdminSignUpRequestDto
import com.assu.app.data.repository.auth.AuthRepository
import com.assu.app.domain.model.auth.LoginModel
import com.assu.app.util.RetrofitResult
import okhttp3.MultipartBody
import javax.inject.Inject

class AdminSignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(request: AdminSignUpRequestDto, signImage: MultipartBody.Part): RetrofitResult<LoginModel> {
        return authRepository.adminSignUp(request, signImage)
    }
}
