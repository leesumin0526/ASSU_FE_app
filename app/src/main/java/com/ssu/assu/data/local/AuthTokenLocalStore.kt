package com.ssu.assu.data.local

import com.ssu.assu.domain.model.auth.LoginModel
import com.ssu.assu.domain.model.auth.UserBasicInfo
import com.ssu.assu.data.dto.UserRole

interface AuthTokenLocalStore {
    
    // 저장 메소드
    fun saveLoginData(loginModel: LoginModel)
    fun updateAccessToken(newAccessToken: String)
    fun updateRefreshToken(newRefreshToken: String)
    fun updateTokens(accessToken: String, refreshToken: String)

    // 초기화 메소드
    fun clearTokens()
    
    // 토큰 관련 getter
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    
    // 사용자 정보 getter
    fun getLoginModel(): LoginModel?
    fun getUserId(): Long
    fun getUserName(): String?
    fun getUserRole(): String?
    fun getUserRoleEnum(): UserRole?
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
    fun isAccessTokenExpiringSoon(): Boolean
    
    // 주기적 로그인 유도 관련 메소드
    fun getLastLoginTime(): Long
    fun updateLastLoginTime()
    fun shouldPromptForReLogin(): Boolean
    fun shouldPromptForReLoginByUserRole(): Boolean
}
