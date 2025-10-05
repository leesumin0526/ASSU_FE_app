package com.assu.app.domain.usecase.auth

import com.assu.app.data.dto.auth.StudentLoginRequestDto
import com.assu.app.domain.model.auth.LoginModel
import com.assu.app.util.RetrofitResult
import com.assu.app.data.repository.auth.AuthRepository
import javax.inject.Inject

class StudentLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    
    suspend operator fun invoke(
        sToken: String,
        sIdno: String
    ): RetrofitResult<LoginModel> {
        if (sToken.isBlank()) {
            return RetrofitResult.Fail(statusCode = -1, code = "VALIDATION_ERROR", message = "유세인트 토큰이 없습니다.")
        }
        
        if (sIdno.isBlank()) {
            return RetrofitResult.Fail(statusCode = -1, code = "VALIDATION_ERROR", message = "학번이 없습니다.")
        }
        
        val request = StudentLoginRequestDto(
            university = "SSU",
            sToken = sToken.trim(),
            sIdno = sIdno.trim()
        )
        
        return authRepository.studentLogin(request)
    }
}
