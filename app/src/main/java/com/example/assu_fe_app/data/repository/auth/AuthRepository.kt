package com.example.assu_fe_app.data.repository.auth

import com.example.assu_fe_app.data.dto.auth.CommonLoginRequestDto
import com.example.assu_fe_app.data.dto.auth.StudentLoginRequestDto
import com.example.assu_fe_app.domain.model.auth.LoginModel
import com.example.assu_fe_app.util.RetrofitResult

interface AuthRepository {
    suspend fun studentLogin(request: StudentLoginRequestDto): RetrofitResult<LoginModel>
    suspend fun commonLogin(request: CommonLoginRequestDto): RetrofitResult<LoginModel>
    suspend fun logout(): RetrofitResult<Unit>
}
