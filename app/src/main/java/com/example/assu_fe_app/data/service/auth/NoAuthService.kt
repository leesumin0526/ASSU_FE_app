package com.example.assu_fe_app.data.service

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.auth.CommonLoginRequestDto
import com.example.assu_fe_app.data.dto.auth.CommonLoginResponseDto
import com.example.assu_fe_app.data.dto.auth.PhoneVerificationSendRequestDto
import com.example.assu_fe_app.data.dto.auth.PhoneVerificationVerifyRequestDto
import com.example.assu_fe_app.data.dto.auth.StudentLoginRequestDto
import com.example.assu_fe_app.data.dto.auth.StudentLoginResponseDto
import com.example.assu_fe_app.data.dto.auth.AdminSignUpResponseDto
import com.example.assu_fe_app.data.dto.auth.PartnerSignUpResponseDto
import com.example.assu_fe_app.data.dto.auth.StudentTokenSignUpRequestDto
import com.example.assu_fe_app.data.dto.auth.StudentTokenSignUpResponseDto
import com.example.assu_fe_app.data.dto.auth.StudentTokenVerifyRequestDto
import com.example.assu_fe_app.data.dto.auth.StudentTokenVerifyResponseDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// 로그인 전용 Service (NoAuth)
interface NoAuthService {
    
    @POST("auth/students/login")
    suspend fun studentLogin(
        @Body request: StudentLoginRequestDto
    ): BaseResponse<StudentLoginResponseDto>
    
    @POST("auth/commons/login")
    suspend fun commonLogin(
        @Body request: CommonLoginRequestDto
    ): BaseResponse<CommonLoginResponseDto>
    
    @POST("auth/phone-verification/send")
    suspend fun sendPhoneVerification(
        @Body request: PhoneVerificationSendRequestDto
    ): BaseResponse<Any>
    
    @POST("auth/phone-verification/verify")
    suspend fun verifyPhoneVerification(
        @Body request: PhoneVerificationVerifyRequestDto
    ): BaseResponse<Any>
    
    @POST("auth/students/signup")
    suspend fun studentSignUp(
        @Body request: StudentTokenSignUpRequestDto
    ): BaseResponse<StudentTokenSignUpResponseDto>
    
    @POST("auth/students/ssu-verify")
    suspend fun verifyStudentToken(
        @Body request: StudentTokenVerifyRequestDto
    ): BaseResponse<StudentTokenVerifyResponseDto>
    
    @Multipart
    @POST("auth/admins/signup")
    suspend fun adminSignUp(
        @Part request: MultipartBody.Part,
        @Part signImage: MultipartBody.Part
    ): BaseResponse<AdminSignUpResponseDto>
    
    @Multipart
    @POST("auth/partners/signup")
    suspend fun partnerSignUp(
        @Part request: MultipartBody.Part,
        @Part licenseImage: MultipartBody.Part
    ): BaseResponse<PartnerSignUpResponseDto>
}
