package com.assu.app.data.dto.auth

import com.assu.app.domain.model.auth.UserBasicInfo
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserBasicInfoDto(
    val name: String,
    val university: String?,
    val department: String?,
    val major: String?
) {
    fun toModel() = UserBasicInfo(
        name = name,
        university = university,
        department = department,
        major = major
    )
}
