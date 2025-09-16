package com.example.assu_fe_app.domain.usecase.auth

import com.example.assu_fe_app.data.dto.auth.StudentTokenSignUpRequestDto
import com.example.assu_fe_app.data.repository.auth.AuthRepository
import com.example.assu_fe_app.domain.model.auth.LoginModel
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class StudentSignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(request: StudentTokenSignUpRequestDto): RetrofitResult<LoginModel> {
        return authRepository.studentSignUp(request)
    }
}
