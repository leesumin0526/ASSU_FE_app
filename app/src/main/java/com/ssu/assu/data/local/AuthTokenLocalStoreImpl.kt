package com.ssu.assu.data.local

import android.content.Context
import android.content.SharedPreferences
import com.ssu.assu.domain.model.auth.LoginModel
import com.ssu.assu.domain.model.auth.UserBasicInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import com.ssu.assu.data.dto.UserRole

@Singleton
class AuthTokenLocalStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AuthTokenLocalStore {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "assu_auth_tokens", 
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_EMAIL = "email"
        private const val KEY_PROFILE_IMAGE_URL = "profile_image_url"
        private const val KEY_STATUS = "status"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_BASIC_INFO_NAME = "basic_info_name"
        private const val KEY_BASIC_INFO_UNIVERSITY = "basic_info_university"
        private const val KEY_BASIC_INFO_DEPARTMENT = "basic_info_department"
        private const val KEY_BASIC_INFO_MAJOR = "basic_info_major"
        private const val KEY_LAST_LOGIN_TIME = "last_login_time"
        
        // 학생 사용자만 주기적 로그인 유도 (7일마다)
        private const val STUDENT_RE_LOGIN_INTERVAL_DAYS = 7
        private const val STUDENT_RE_LOGIN_INTERVAL_MS = STUDENT_RE_LOGIN_INTERVAL_DAYS * 24 * 60 * 60 * 1000L
    }

    /****************************** 저장 메소드 ******************************/
    override fun saveLoginData(loginModel: LoginModel) {
        android.util.Log.d("AuthTokenLocalStore", "=== saveLoginData 시작 ===")
        android.util.Log.d("AuthTokenLocalStore", "SharedPreferences 파일명: assu_auth_tokens")
        android.util.Log.d("AuthTokenLocalStore", "Access Token: ${loginModel.accessToken?.take(20)}...")
        android.util.Log.d("AuthTokenLocalStore", "Refresh Token: ${loginModel.refreshToken?.take(20)}...")
        android.util.Log.d("AuthTokenLocalStore", "User ID: ${loginModel.userId}")
        android.util.Log.d("AuthTokenLocalStore", "Username: ${loginModel.username}")
        android.util.Log.d("AuthTokenLocalStore", "User Role: ${loginModel.userRole}")
        android.util.Log.d("AuthTokenLocalStore", "Status: ${loginModel.status}")
        
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, loginModel.accessToken)
            putString(KEY_REFRESH_TOKEN, loginModel.refreshToken)
            putLong(KEY_USER_ID, loginModel.userId)
            putString(KEY_USERNAME, loginModel.username)
            putString(KEY_USER_ROLE, loginModel.userRole)
            putString(KEY_EMAIL, loginModel.email)
            putString(KEY_PROFILE_IMAGE_URL, loginModel.profileImageUrl)
            putString(KEY_STATUS, loginModel.status)
            putBoolean(KEY_IS_LOGGED_IN, true)
            
            // basicInfo 저장
            loginModel.basicInfo?.let { basicInfo ->
                android.util.Log.d("AuthTokenLocalStore", "Basic Info - Name: ${basicInfo.name}")
                putString(KEY_BASIC_INFO_NAME, basicInfo.name)
                putString(KEY_BASIC_INFO_UNIVERSITY, basicInfo.university)
                putString(KEY_BASIC_INFO_DEPARTMENT, basicInfo.department)
                putString(KEY_BASIC_INFO_MAJOR, basicInfo.major)
            }
        }.apply()
        
        // 로그인 시간 저장
        updateLastLoginTime()
        
        android.util.Log.d("AuthTokenLocalStore", "=== saveLoginData 완료 ===")
    }

    override fun updateAccessToken(newAccessToken: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, newAccessToken).apply()
    }

    override fun updateRefreshToken(newRefreshToken: String) {
        prefs.edit().putString(KEY_REFRESH_TOKEN, newRefreshToken).apply()
    }

    override fun updateTokens(accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    /****************************** 초기화 메소드 ******************************/
    override fun clearTokens() {
        android.util.Log.d("AuthTokenLocalStore", "clearTokens() called - clearing all auth data")
        prefs.edit().clear().apply()
        android.util.Log.d("AuthTokenLocalStore", "clearTokens() completed - all tokens cleared")
    }

    /****************************** 토큰 관련 getter ******************************/
    override fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    override fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    /****************************** 사용자 정보 getter ******************************/
    override fun getLoginModel(): LoginModel? {
        android.util.Log.d("AuthTokenLocalStore", "=== getLoginModel 시작 ===")
        
        val accessToken = prefs.getString(KEY_ACCESS_TOKEN, null)
        android.util.Log.d("AuthTokenLocalStore", "읽은 Access Token: ${accessToken?.take(20)}...")
        val refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null)

        if (accessToken == null || refreshToken == null) {
            android.util.Log.w("AuthTokenLocalStore", "Access Token이 null이므로 null 반환")
            return null
        }

        val userId = prefs.getLong(KEY_USER_ID, -1L)
        val username = prefs.getString(KEY_USERNAME, null) ?: ""
        val userRole = prefs.getString(KEY_USER_ROLE, null) ?: ""
        val email = prefs.getString(KEY_EMAIL, null)
        val profileImageUrl = prefs.getString(KEY_PROFILE_IMAGE_URL, null)
        val status = prefs.getString(KEY_STATUS, null)
        
        // null 체크 - username과 userRole이 null이면 LoginModel을 생성할 수 없음
        if (username.isEmpty() || userRole.isEmpty()) {
            android.util.Log.w("AuthTokenLocalStore", "Username 또는 UserRole이 비어있음 - Username: '$username', UserRole: '$userRole'")
            return null
        }
        
        android.util.Log.d("AuthTokenLocalStore", "읽은 값들 - User ID: $userId, Username: $username, Role: $userRole")
        
        // basicInfo 조회
        val basicInfoName = prefs.getString(KEY_BASIC_INFO_NAME, null)
        val basicInfo = if (basicInfoName != null) {
            UserBasicInfo(
                name = basicInfoName,
                university = prefs.getString(KEY_BASIC_INFO_UNIVERSITY, null),
                department = prefs.getString(KEY_BASIC_INFO_DEPARTMENT, null),
                major = prefs.getString(KEY_BASIC_INFO_MAJOR, null)
            )
        } else null
        
        android.util.Log.d("AuthTokenLocalStore", "=== getLoginModel 완료 ===")
        
        return LoginModel(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId,
            username = username,
            userRole = userRole,
            email = email,
            profileImageUrl = profileImageUrl,
            status = status,
            basicInfo = basicInfo
        )
    }

    override fun getUserId(): Long {
        return prefs.getLong(KEY_USER_ID, -1L)
    }

    override fun getUserName(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    override fun getUserRole(): String? {
        return prefs.getString(KEY_USER_ROLE, null)
    }

    override fun getUserRoleEnum(): UserRole? {
        val roleString = prefs.getString(KEY_USER_ROLE, null)
        return when (roleString?.uppercase()) {
            "ADMIN" -> UserRole.ADMIN
            "PARTNER" -> UserRole.PARTNER
            "STUDENT" -> UserRole.STUDENT
            else -> null
        }
    }

    override fun getEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    override fun getProfileImageUrl(): String? {
        return prefs.getString(KEY_PROFILE_IMAGE_URL, null)
    }

    override fun getStatus(): String? {
        return prefs.getString(KEY_STATUS, null)
    }

    /****************************** 기본 정보 getter ******************************/
    override fun getBasicInfo(): UserBasicInfo? {
        val basicInfoName = prefs.getString(KEY_BASIC_INFO_NAME, null)
        return if (basicInfoName != null) {
            UserBasicInfo(
                name = basicInfoName,
                university = prefs.getString(KEY_BASIC_INFO_UNIVERSITY, null),
                department = prefs.getString(KEY_BASIC_INFO_DEPARTMENT, null),
                major = prefs.getString(KEY_BASIC_INFO_MAJOR, null)
            )
        } else null
    }

    override fun getBasicInfoName(): String? {
        return prefs.getString(KEY_BASIC_INFO_NAME, null)
    }

    override fun getBasicInfoUniversity(): String? {
        return prefs.getString(KEY_BASIC_INFO_UNIVERSITY, null)
    }

    override fun getBasicInfoDepartment(): String? {
        return prefs.getString(KEY_BASIC_INFO_DEPARTMENT, null)
    }

    override fun getBasicInfoMajor(): String? {
        return prefs.getString(KEY_BASIC_INFO_MAJOR, null)
    }

    /****************************** 상태 관련 메소드 ******************************/
    override fun isLoggedIn(): Boolean {
        val isLoggedInFlag = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        val accessToken = getAccessToken()
        val result = isLoggedInFlag && accessToken != null
        
        android.util.Log.d("AuthTokenLocalStore", "=== isLoggedIn() 체크 ===")
        android.util.Log.d("AuthTokenLocalStore", "KEY_IS_LOGGED_IN: $isLoggedInFlag")
        android.util.Log.d("AuthTokenLocalStore", "Access Token 존재: ${accessToken != null}")
        android.util.Log.d("AuthTokenLocalStore", "최종 결과: $result")
        
        return result
    }

    override fun isAccessTokenExpired(): Boolean {
        val accessToken = getAccessToken() ?: return true
        
        try {
            val exp = getTokenExpirationTime(accessToken)
            if (exp == null) return true
            
            // 현재 시간과 비교 (초 단위)
            val currentTime = System.currentTimeMillis() / 1000
            val isExpired = currentTime >= exp
            
            android.util.Log.d("AuthTokenLocalStore", "Token exp: $exp, current: $currentTime, expired: $isExpired")
            return isExpired
            
        } catch (e: Exception) {
            android.util.Log.e("AuthTokenLocalStore", "Error checking token expiration: ${e.message}")
            return true // 파싱 오류 시 만료된 것으로 간주
        }
    }

    override fun isAccessTokenExpiringSoon(): Boolean {
        val accessToken = getAccessToken() ?: return true
        
        try {
            val exp = getTokenExpirationTime(accessToken)
            if (exp == null) return true
            
            // 현재 시간 + 5분 (300초)
            val currentTime = System.currentTimeMillis() / 1000
            val bufferTime = 300L // 5분
            val isExpiringSoon = currentTime >= (exp - bufferTime)
            
            android.util.Log.d("AuthTokenLocalStore", "Token exp: $exp, current: $currentTime, expiring soon: $isExpiringSoon")
            return isExpiringSoon
            
        } catch (e: Exception) {
            android.util.Log.e("AuthTokenLocalStore", "Error checking token expiring soon: ${e.message}")
            return true // 파싱 오류 시 만료된 것으로 간주
        }
    }
    
    /**
     * JWT 토큰에서 만료 시간 추출
     */
    private fun getTokenExpirationTime(token: String): Long? {
        return try {
            // JWT 토큰의 payload 부분 디코딩
            val parts = token.split(".")
            if (parts.size != 3) return null
            
            val payload = parts[1]
            // Base64 URL 디코딩
            val decodedBytes = android.util.Base64.decode(payload, android.util.Base64.URL_SAFE)
            val payloadJson = String(decodedBytes)
            
            // JSON 파싱하여 exp 필드 추출
            val jsonObject = org.json.JSONObject(payloadJson)
            jsonObject.getLong("exp")
            
        } catch (e: Exception) {
            android.util.Log.e("AuthTokenLocalStore", "Error parsing token expiration: ${e.message}")
            null
        }
    }

    /****************************** 주기적 로그인 유도 관련 메소드 ******************************/
    override fun getLastLoginTime(): Long {
        return prefs.getLong(KEY_LAST_LOGIN_TIME, 0L)
    }

    override fun updateLastLoginTime() {
        val currentTime = System.currentTimeMillis()
        prefs.edit().putLong(KEY_LAST_LOGIN_TIME, currentTime).apply()
        android.util.Log.d("AuthTokenLocalStore", "Last login time updated: $currentTime")
    }

    override fun shouldPromptForReLogin(): Boolean {
        val lastLoginTime = getLastLoginTime()
        if (lastLoginTime == 0L) {
            android.util.Log.d("AuthTokenLocalStore", "No previous login time found")
            return false
        }

        val currentTime = System.currentTimeMillis()
        val timeSinceLastLogin = currentTime - lastLoginTime
        val shouldPrompt = timeSinceLastLogin >= STUDENT_RE_LOGIN_INTERVAL_MS

        android.util.Log.d("AuthTokenLocalStore", 
            "Time since last login: ${timeSinceLastLogin / (1000 * 60 * 60 * 24)} days, " +
            "should prompt: $shouldPrompt"
        )

        return shouldPrompt
    }

    override fun shouldPromptForReLoginByUserRole(): Boolean {
        val userRole = getUserRole()
        
        // 학생 사용자가 아니면 주기적 로그인 유도하지 않음
        if (userRole != "STUDENT") {
            android.util.Log.d("AuthTokenLocalStore", "User role is $userRole, skipping periodic re-login")
            return false
        }
        
        // 학생 사용자만 주기적 로그인 유도 적용
        return shouldPromptForReLogin()
    }
}
