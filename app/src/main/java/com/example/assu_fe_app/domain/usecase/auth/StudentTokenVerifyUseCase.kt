package com.example.assu_fe_app.domain.usecase.auth

import com.example.assu_fe_app.data.dto.auth.StudentTokenVerifyRequestDto
import com.example.assu_fe_app.data.dto.auth.StudentTokenVerifyResponseDto
import com.example.assu_fe_app.data.repository.auth.AuthRepository
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class StudentTokenVerifyUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(request: StudentTokenVerifyRequestDto): RetrofitResult<StudentTokenVerifyResponseDto> {
        return authRepository.verifyStudentToken(request)
    }
}
