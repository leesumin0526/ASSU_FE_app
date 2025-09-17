package com.example.assu_fe_app.data.repositoryImpl

import com.example.assu_fe_app.data.dto.auth.CommonLoginRequestDto
import com.example.assu_fe_app.data.dto.auth.PhoneVerificationSendRequestDto
import com.example.assu_fe_app.data.dto.auth.PhoneVerificationVerifyRequestDto
import com.example.assu_fe_app.data.dto.auth.StudentLoginRequestDto
import com.example.assu_fe_app.data.repository.auth.AuthRepository
import com.example.assu_fe_app.data.service.AuthService
import com.example.assu_fe_app.data.service.NoAuthService
import com.example.assu_fe_app.domain.model.auth.LoginModel
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
import com.example.assu_fe_app.util.apiHandlerForUnit
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val noAuthService: NoAuthService,
    private val authService: AuthService
) : AuthRepository {
    
    override suspend fun studentLogin(request: StudentLoginRequestDto): RetrofitResult<LoginModel> {
        return apiHandler(
            execute = { noAuthService.studentLogin(request) },
            mapper = { response -> response.toModel() }
        )
    }
    
    override suspend fun commonLogin(request: CommonLoginRequestDto): RetrofitResult<LoginModel> {
        return apiHandler(
            execute = { noAuthService.commonLogin(request) },
            mapper = { response -> response.toModel() }
        )
    }
    
    override suspend fun logout(): RetrofitResult<Unit> {
        return apiHandlerForUnit(
            execute = { authService.logout() },
            mapper = { Unit }
        )
    }
    
    override suspend fun withdraw(): RetrofitResult<Unit> {
        return apiHandlerForUnit(
            execute = { authService.withdraw() },
            mapper = { Unit }
        )
    }
    
    override suspend fun sendPhoneVerification(request: PhoneVerificationSendRequestDto): RetrofitResult<Unit> {
        return apiHandlerForUnit(
            execute = { noAuthService.sendPhoneVerification(request) },
            mapper = { Unit }
        )
    }
    
    override suspend fun verifyPhoneVerification(request: PhoneVerificationVerifyRequestDto): RetrofitResult<Unit> {
        return apiHandlerForUnit(
            execute = { noAuthService.verifyPhoneVerification(request) },
            mapper = { Unit }
        )
    }
}
