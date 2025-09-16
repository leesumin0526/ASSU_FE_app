package com.example.assu_fe_app.data.local

import com.example.assu_fe_app.domain.model.auth.LoginModel
import com.example.assu_fe_app.domain.model.auth.UserBasicInfo

interface AuthTokenLocalStore {
    
    // 저장 메소드
    fun saveLoginData(loginModel: LoginModel)
    fun updateAccessToken(newAccessToken: String)

    // 초기화 메소드
    fun clearTokens()
    
    // 토큰 관련 getter
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    
    // 사용자 정보 getter
    fun getLoginModel(): LoginModel?
    fun getUserId(): Long
    fun getUsername(): String?
    fun getUserRole(): String?
    fun getEmail(): String?
    fun getProfileImageUrl(): String?
    fun getStatus(): String?
    
    // 기본 정보 getter
    fun getBasicInfo(): UserBasicInfo?
    fun getBasicInfoName(): String?
    fun getBasicInfoUniversity(): String?
    fun getBasicInfoDepartment(): String?
    fun getBasicInfoMajor(): String?
    
    // 상태 관련 메소드
    fun isLoggedIn(): Boolean
    fun isAccessTokenExpired(): Boolean
}
