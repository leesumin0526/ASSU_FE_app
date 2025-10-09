package com.ssu.assu.data.dto.auth

import com.ssu.assu.domain.model.auth.LoginModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StudentLoginResponseDto(
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
}

