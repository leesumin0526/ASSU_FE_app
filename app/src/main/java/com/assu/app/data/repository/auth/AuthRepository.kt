package com.assu.app.data.repository.auth

import com.assu.app.data.dto.auth.CommonLoginRequestDto
import com.assu.app.data.dto.auth.PhoneVerificationSendRequestDto
import com.assu.app.data.dto.auth.PhoneVerificationVerifyRequestDto
import com.assu.app.data.dto.auth.StudentLoginRequestDto
import com.assu.app.data.dto.auth.AdminSignUpRequestDto
import com.assu.app.data.dto.auth.PartnerSignUpRequestDto
import com.assu.app.data.dto.auth.StudentTokenSignUpRequestDto
import com.assu.app.data.dto.auth.StudentTokenVerifyRequestDto
import com.assu.app.data.dto.auth.StudentTokenVerifyResponseDto
import com.assu.app.data.dto.auth.EmailVerificationRequestDto
import okhttp3.MultipartBody
import com.assu.app.domain.model.auth.LoginModel
import com.assu.app.util.RetrofitResult

interface AuthRepository {
    suspend fun studentLogin(request: StudentLoginRequestDto): RetrofitResult<LoginModel>
    suspend fun commonLogin(request: CommonLoginRequestDto): RetrofitResult<LoginModel>
    suspend fun logout(): RetrofitResult<Unit>
    suspend fun withdraw(): RetrofitResult<Unit>
    suspend fun checkAndSendPhoneVerification(request: PhoneVerificationSendRequestDto): RetrofitResult<Unit>
    suspend fun verifyPhoneVerification(request: PhoneVerificationVerifyRequestDto): RetrofitResult<Unit>
    suspend fun studentSignUp(request: StudentTokenSignUpRequestDto): RetrofitResult<LoginModel>
    suspend fun verifyStudentToken(request: StudentTokenVerifyRequestDto): RetrofitResult<StudentTokenVerifyResponseDto>
    suspend fun adminSignUp(request: AdminSignUpRequestDto, signImage: MultipartBody.Part): RetrofitResult<LoginModel>
    suspend fun partnerSignUp(request: PartnerSignUpRequestDto, licenseImage: MultipartBody.Part): RetrofitResult<LoginModel>
    suspend fun checkEmailVerification(request: EmailVerificationRequestDto): RetrofitResult<Unit>
}
