package com.example.assu_fe_app.data.repositoryImpl

import com.example.assu_fe_app.data.dto.auth.CommonLoginRequestDto
import com.example.assu_fe_app.data.dto.auth.PhoneVerificationSendRequestDto
import com.example.assu_fe_app.data.dto.auth.PhoneVerificationVerifyRequestDto
import com.example.assu_fe_app.data.dto.auth.StudentLoginRequestDto
import com.example.assu_fe_app.data.dto.auth.AdminSignUpRequestDto
import com.example.assu_fe_app.data.dto.auth.PartnerSignUpRequestDto
import com.example.assu_fe_app.data.dto.auth.StudentTokenSignUpRequestDto
import com.example.assu_fe_app.data.dto.auth.StudentTokenVerifyRequestDto
import com.example.assu_fe_app.data.dto.auth.StudentTokenVerifyResponseDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.example.assu_fe_app.data.service.AuthService
import com.example.assu_fe_app.data.service.NoAuthService
import com.example.assu_fe_app.domain.model.auth.LoginModel
import com.example.assu_fe_app.data.repository.auth.AuthRepository
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

    override suspend fun studentSignUp(request: StudentTokenSignUpRequestDto): RetrofitResult<LoginModel> {
        return apiHandler(
            execute = { noAuthService.studentSignUp(request) },
            mapper = { response -> response.toModel() }
        )
    }

    override suspend fun verifyStudentToken(request: StudentTokenVerifyRequestDto): RetrofitResult<StudentTokenVerifyResponseDto> {
        return apiHandler(
            execute = { noAuthService.verifyStudentToken(request) },
            mapper = { response -> response }
        )
    }

    override suspend fun adminSignUp(request: AdminSignUpRequestDto, signImage: MultipartBody.Part): RetrofitResult<LoginModel> {
        // JSON을 RequestBody로 변환
        val requestJson = com.google.gson.Gson().toJson(request)
        val requestBody = requestJson.toRequestBody("application/json".toMediaTypeOrNull())
        val requestPart = MultipartBody.Part.createFormData("request", null, requestBody)

        return apiHandler(
            execute = { noAuthService.adminSignUp(requestPart, signImage) },
            mapper = { response -> response.toModel() }
        )
    }

    override suspend fun partnerSignUp(request: PartnerSignUpRequestDto, licenseImage: MultipartBody.Part): RetrofitResult<LoginModel> {
        // JSON을 RequestBody로 변환
        val requestJson = com.google.gson.Gson().toJson(request)
        val requestBody = requestJson.toRequestBody("application/json".toMediaTypeOrNull())
        val requestPart = MultipartBody.Part.createFormData("request", null, requestBody)

        return apiHandler(
            execute = { noAuthService.partnerSignUp(requestPart, licenseImage) },
            mapper = { response -> response.toModel() }
        )
    }
}
