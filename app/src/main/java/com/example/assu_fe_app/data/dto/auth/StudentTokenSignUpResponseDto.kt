package com.example.assu_fe_app.data.dto.auth

import com.example.assu_fe_app.domain.model.auth.LoginModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StudentTokenSignUpResponseDto(
    val memberId: Long,
    val role: String,
    val status: String,
    val tokens: TokenDto,
    val basicInfo: UserBasicInfoDto
) {
    fun toModel() = LoginModel(
        accessToken = tokens.accessToken,
        refreshToken = tokens.refreshToken,
        userId = memberId,
        username = basicInfo.name,
        userRole = role,
        email = null,
        profileImageUrl = null,
        status = status,
        basicInfo = basicInfo.toModel()
    )
    
    val isActive: Boolean
        get() = status == "ACTIVE"
}

