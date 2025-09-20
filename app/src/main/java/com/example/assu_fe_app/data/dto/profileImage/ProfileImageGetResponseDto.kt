package com.example.assu_fe_app.data.dto.profileImage


import com.example.assu_fe_app.domain.model.profileImage.ProfileImageModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileImageGetResponseDto(
    val url: String
) {
    fun toModel() = ProfileImageGetResponseDto(
        url = url
    )
}