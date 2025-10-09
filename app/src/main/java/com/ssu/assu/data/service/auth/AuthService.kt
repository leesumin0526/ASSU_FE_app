package com.ssu.assu.data.service

import com.ssu.assu.data.dto.BaseResponse
import com.ssu.assu.data.dto.auth.CommonLoginRequestDto
import com.ssu.assu.data.dto.auth.CommonLoginResponseDto
import com.ssu.assu.data.dto.auth.PhoneVerificationSendRequestDto
import com.ssu.assu.data.dto.auth.PhoneVerificationVerifyRequestDto
import com.ssu.assu.data.dto.auth.StudentLoginRequestDto
import com.ssu.assu.data.dto.auth.StudentLoginResponseDto
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthService {
    
    @POST("auth/students/login")
    suspend fun studentLogin(
        @Body request: StudentLoginRequestDto
    ): BaseResponse<StudentLoginResponseDto>
    
    @POST("auth/commons/login")
    suspend fun commonLogin(
        @Body request: CommonLoginRequestDto
    ): BaseResponse<CommonLoginResponseDto>
    
    @POST("auth/logout")
    suspend fun logout(): BaseResponse<Any>
    
    @POST("auth/phone-verification/check-and-send")
    suspend fun checkAndSendPhoneVerification(
        @Body request: PhoneVerificationSendRequestDto
    ): BaseResponse<Any>
    
    @POST("auth/phone-verification/verify")
    suspend fun verifyPhoneVerification(
        @Body request: PhoneVerificationVerifyRequestDto
    ): BaseResponse<Any>
    
    @PATCH("auth/withdraw")
    suspend fun withdraw(): BaseResponse<Any>
}
