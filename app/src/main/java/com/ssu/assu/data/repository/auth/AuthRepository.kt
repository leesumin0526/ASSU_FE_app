package com.ssu.assu.data.repository.auth

import com.ssu.assu.data.dto.auth.CommonLoginRequestDto
import com.ssu.assu.data.dto.auth.PhoneVerificationSendRequestDto
import com.ssu.assu.data.dto.auth.PhoneVerificationVerifyRequestDto
import com.ssu.assu.data.dto.auth.StudentLoginRequestDto
import com.ssu.assu.data.dto.auth.AdminSignUpRequestDto
import com.ssu.assu.data.dto.auth.PartnerSignUpRequestDto
import com.ssu.assu.data.dto.auth.StudentTokenSignUpRequestDto
import com.ssu.assu.data.dto.auth.StudentTokenVerifyRequestDto
import com.ssu.assu.data.dto.auth.StudentTokenVerifyResponseDto
import com.ssu.assu.data.dto.auth.EmailVerificationRequestDto
import okhttp3.MultipartBody
import com.ssu.assu.domain.model.auth.LoginModel
import com.ssu.assu.util.RetrofitResult

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
