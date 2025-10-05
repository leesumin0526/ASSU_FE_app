package com.assu.app.domain.usecase.auth

import com.assu.app.data.dto.auth.StudentTokenSignUpRequestDto
import com.assu.app.data.repository.auth.AuthRepository
import com.assu.app.domain.model.auth.LoginModel
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class StudentSignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(request: StudentTokenSignUpRequestDto): RetrofitResult<LoginModel> {
        return authRepository.studentSignUp(request)
    }
}
