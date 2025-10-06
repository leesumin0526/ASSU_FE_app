package com.ssu.assu.data.repositoryImpl

import android.util.Log
import com.ssu.assu.data.dto.auth.AdminSignUpRequestDto
import com.ssu.assu.data.dto.auth.CommonLoginRequestDto
import com.ssu.assu.data.dto.auth.EmailVerificationRequestDto
import com.ssu.assu.data.dto.auth.PartnerSignUpRequestDto
import com.ssu.assu.data.dto.auth.PhoneVerificationSendRequestDto
import com.ssu.assu.data.dto.auth.PhoneVerificationVerifyRequestDto
import com.ssu.assu.data.dto.auth.StudentLoginRequestDto
import com.ssu.assu.data.dto.auth.StudentTokenSignUpRequestDto
import com.ssu.assu.data.dto.auth.StudentTokenVerifyRequestDto
import com.ssu.assu.data.dto.auth.StudentTokenVerifyResponseDto
import com.ssu.assu.data.repository.auth.AuthRepository
import com.ssu.assu.data.service.AuthService
import com.ssu.assu.data.service.NoAuthService
import com.ssu.assu.domain.model.auth.LoginModel
import com.ssu.assu.util.RetrofitResult
import com.ssu.assu.util.apiHandler
import com.ssu.assu.util.apiHandlerForUnit
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
