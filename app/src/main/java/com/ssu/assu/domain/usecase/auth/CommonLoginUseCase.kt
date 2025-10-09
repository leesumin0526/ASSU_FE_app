package com.ssu.assu.domain.usecase.auth

import com.ssu.assu.data.dto.auth.CommonLoginRequestDto
import com.ssu.assu.data.repository.auth.AuthRepository
import com.ssu.assu.domain.model.auth.LoginModel
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class CommonLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    
    suspend operator fun invoke(
        email: String,
        password: String
    ): RetrofitResult<LoginModel> {
        if (email.isBlank()) {
            return RetrofitResult.Fail(statusCode = -1, code = "VALIDATION_ERROR", message = "이메일을 입력해주세요.")
        }
        
        if (password.isBlank()) {
            return RetrofitResult.Fail(statusCode = -1, code = "VALIDATION_ERROR", message = "비밀번호를 입력해주세요.")
        }
        
        if (!isValidEmail(email)) {
            return RetrofitResult.Fail(statusCode = -1, code = "VALIDATION_ERROR", message = "올바른 이메일 형식을 입력해주세요.")
        }
        
        if (password.length < 6) {
            return RetrofitResult.Fail(statusCode = -1, code = "VALIDATION_ERROR", message = "비밀번호는 6자 이상이어야 합니다.")
        }
        
        val request = CommonLoginRequestDto(
            email = email.trim(),
            password = password
        )
        
        return authRepository.commonLogin(request)
    }
    
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$".toRegex()
        return emailRegex.matches(email)
    }
}
