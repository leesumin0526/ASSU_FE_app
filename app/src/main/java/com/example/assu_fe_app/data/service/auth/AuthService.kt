package com.example.assu_fe_app.data.service

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.auth.CommonLoginRequestDto
import com.example.assu_fe_app.data.dto.auth.CommonLoginResponseDto
import com.example.assu_fe_app.data.dto.auth.StudentLoginRequestDto
import com.example.assu_fe_app.data.dto.auth.StudentLoginResponseDto
import retrofit2.http.Body
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
    suspend fun logout(): BaseResponse<Unit>
}
