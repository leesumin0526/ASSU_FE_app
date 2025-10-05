package com.assu.app.domain.model.auth

data class LoginModel(
    val accessToken: String,
    val refreshToken: String,
    val userId: Long,
    val username: String,
    val userRole: String,
    val email: String?,
    val profileImageUrl: String?,
    val status: String? = null,
    val basicInfo: UserBasicInfo? = null
) {
    val isAdmin: Boolean
        get() = userRole == "ADMIN"
    
    val isPartner: Boolean
        get() = userRole == "PARTNER"
    
    val isStudent: Boolean
        get() = userRole == "STUDENT"
    
    val isActive: Boolean
        get() = status == "ACTIVE"
    
    val isPendingApproval: Boolean
        get() = status == "SUSPEND"
    
    val displayName: String
        get() = email ?: username
}
