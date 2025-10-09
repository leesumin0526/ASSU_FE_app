package com.ssu.assu.domain.usecase.auth

import com.ssu.assu.data.dto.auth.StudentTokenSignUpRequestDto
import com.ssu.assu.data.repository.auth.AuthRepository
import com.ssu.assu.domain.model.auth.LoginModel
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class StudentSignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(request: StudentTokenSignUpRequestDto): RetrofitResult<LoginModel> {
        return authRepository.studentSignUp(request)
    }
}
