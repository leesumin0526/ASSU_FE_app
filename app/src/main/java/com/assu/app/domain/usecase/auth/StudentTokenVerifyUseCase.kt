package com.assu.app.domain.usecase.auth

import com.assu.app.data.dto.auth.StudentTokenVerifyRequestDto
import com.assu.app.data.dto.auth.StudentTokenVerifyResponseDto
import com.assu.app.data.repository.auth.AuthRepository
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class StudentTokenVerifyUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(request: StudentTokenVerifyRequestDto): RetrofitResult<StudentTokenVerifyResponseDto> {
        return authRepository.verifyStudentToken(request)
    }
}
