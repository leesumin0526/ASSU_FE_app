package com.assu.app.data.repositoryImpl

import android.util.Log
import com.assu.app.data.dto.auth.AdminSignUpRequestDto
import com.assu.app.data.dto.auth.CommonLoginRequestDto
import com.assu.app.data.dto.auth.EmailVerificationRequestDto
import com.assu.app.data.dto.auth.PartnerSignUpRequestDto
import com.assu.app.data.dto.auth.PhoneVerificationSendRequestDto
import com.assu.app.data.dto.auth.PhoneVerificationVerifyRequestDto
import com.assu.app.data.dto.auth.StudentLoginRequestDto
import com.assu.app.data.dto.auth.StudentTokenSignUpRequestDto
import com.assu.app.data.dto.auth.StudentTokenVerifyRequestDto
import com.assu.app.data.dto.auth.StudentTokenVerifyResponseDto
import com.assu.app.data.repository.auth.AuthRepository
import com.assu.app.data.service.AuthService
import com.assu.app.data.service.NoAuthService
import com.assu.app.domain.model.auth.LoginModel
import com.assu.app.util.RetrofitResult
import com.assu.app.util.apiHandler
import com.assu.app.util.apiHandlerForUnit
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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
    
    override suspend fun checkAndSendPhoneVerification(request: PhoneVerificationSendRequestDto): RetrofitResult<Unit> {
        return apiHandlerForUnit(
            execute = { noAuthService.checkAndSendPhoneVerification(request) },
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
        
        // 실제 서버로 전송되는 JSON 로그 출력
        Log.d("AuthRepositoryImpl", "=== 관리자 회원가입 서버 전송 JSON ===")
        Log.d("AuthRepositoryImpl", "📤 Request JSON:")
        Log.d("AuthRepositoryImpl", requestJson)
        Log.d("AuthRepositoryImpl", "📏 JSON 길이: ${requestJson.length}")
        Log.d("AuthRepositoryImpl", "==========================================")
        
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

    override suspend fun checkEmailVerification(request: EmailVerificationRequestDto): RetrofitResult<Unit> {
        return apiHandlerForUnit(
            execute = { noAuthService.checkEmailVerification(request) },
            mapper = { Unit }
        )
    }
}
