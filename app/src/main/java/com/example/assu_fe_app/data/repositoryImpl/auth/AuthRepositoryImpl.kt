package com.example.assu_fe_app.data.repositoryImpl

import com.example.assu_fe_app.data.dto.auth.CommonLoginRequestDto
import com.example.assu_fe_app.data.dto.auth.PhoneVerificationSendRequestDto
import com.example.assu_fe_app.data.dto.auth.PhoneVerificationVerifyRequestDto
import com.example.assu_fe_app.data.dto.auth.StudentLoginRequestDto
import com.example.assu_fe_app.data.service.AuthService
import com.example.assu_fe_app.domain.model.auth.LoginModel
import com.example.assu_fe_app.data.repository.auth.AuthRepository
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService
) : AuthRepository {
    
    override suspend fun studentLogin(request: StudentLoginRequestDto): RetrofitResult<LoginModel> {
        return apiHandler(
            execute = { authService.studentLogin(request) },
            mapper = { response -> response.toModel() }
        )
    }
    
    override suspend fun commonLogin(request: CommonLoginRequestDto): RetrofitResult<LoginModel> {
        return apiHandler(
            execute = { authService.commonLogin(request) },
            mapper = { response -> response.toModel() }
        )
    }
    
    override suspend fun logout(): RetrofitResult<Unit> {
        return apiHandler(
            execute = { authService.logout() },
            mapper = { Unit }
        )
    }
    
    override suspend fun sendPhoneVerification(request: PhoneVerificationSendRequestDto): RetrofitResult<Unit> {
        return apiHandler(
            execute = { authService.sendPhoneVerification(request) },
            mapper = { Unit }
        )
    }
    
    override suspend fun verifyPhoneVerification(request: PhoneVerificationVerifyRequestDto): RetrofitResult<Unit> {
        return apiHandler(
            execute = { authService.verifyPhoneVerification(request) },
            mapper = { Unit }
        )
    }
}
