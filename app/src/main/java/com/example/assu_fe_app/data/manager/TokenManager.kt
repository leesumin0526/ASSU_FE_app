package com.example.assu_fe_app.data.manager

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.assu_fe_app.domain.model.auth.LoginModel
import com.example.assu_fe_app.domain.model.auth.UserBasicInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "auth_tokens", 
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
        private const val KEY_DEVICE_TOKEN_ID = "device_token_id"
        private const val KEY_BASIC_INFO_NAME = "basic_info_name"
        private const val KEY_BASIC_INFO_UNIVERSITY = "basic_info_university"
        private const val KEY_BASIC_INFO_DEPARTMENT = "basic_info_department"
        private const val KEY_BASIC_INFO_MAJOR = "basic_info_major"
    }
    
    fun saveLoginData(loginModel: LoginModel) {
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
                putString(KEY_BASIC_INFO_NAME, basicInfo.name)
                putString(KEY_BASIC_INFO_UNIVERSITY, basicInfo.university)
                putString(KEY_BASIC_INFO_DEPARTMENT, basicInfo.department)
                putString(KEY_BASIC_INFO_MAJOR, basicInfo.major)
            }
            
            apply()
        }
    }
    
    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }
    
    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }
    
    fun getLoginModel(): LoginModel? {
        val accessToken = prefs.getString(KEY_ACCESS_TOKEN, null) ?: return null
        val refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null) ?: return null
        val userId = prefs.getLong(KEY_USER_ID, -1L)
        val username = prefs.getString(KEY_USERNAME, "") ?: ""
        val userRole = prefs.getString(KEY_USER_ROLE, "") ?: ""
        val email = prefs.getString(KEY_EMAIL, null)
        val profileImageUrl = prefs.getString(KEY_PROFILE_IMAGE_URL, null)
        val status = prefs.getString(KEY_STATUS, null)
        
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
    
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && getAccessToken() != null
    }
    
    fun clearTokens() {
        prefs.edit().clear().apply()
    }
    
    fun updateAccessToken(newAccessToken: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, newAccessToken).apply()
    }
    
    fun getUserRole(): String? {
        return prefs.getString(KEY_USER_ROLE, null)
    }
    
    fun getUserId(): Long {
        return prefs.getLong(KEY_USER_ID, -1L)
    }

    fun getUserName(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }
    
    fun saveDeviceTokenId(tokenId: Long) {
        prefs.edit { putLong(KEY_DEVICE_TOKEN_ID, tokenId) }
    }
    
    fun getDeviceTokenId(): Long? {
        val tokenId = prefs.getLong(KEY_DEVICE_TOKEN_ID, -1L)
        return if (tokenId == -1L) null else tokenId
    }
}
