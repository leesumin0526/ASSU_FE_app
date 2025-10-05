package com.assu.app.data.dto.profileImage


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileImageGetResponseDto(
    val url: String
) {
    fun toModel() = ProfileImageGetResponseDto(
        url = url
    )
}