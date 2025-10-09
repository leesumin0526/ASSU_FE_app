package com.ssu.assu.domain.usecase.auth

import com.ssu.assu.data.dto.auth.StudentTokenVerifyRequestDto
import com.ssu.assu.data.dto.auth.StudentTokenVerifyResponseDto
import com.ssu.assu.data.repository.auth.AuthRepository
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class StudentTokenVerifyUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(request: StudentTokenVerifyRequestDto): RetrofitResult<StudentTokenVerifyResponseDto> {
        return authRepository.verifyStudentToken(request)
    }
}
