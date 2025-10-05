package com.assu.app.data.dto.auth

import com.assu.app.domain.model.auth.LoginModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PartnerSignUpResponseDto(
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
