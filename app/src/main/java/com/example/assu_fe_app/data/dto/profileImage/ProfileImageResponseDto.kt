package com.example.assu_fe_app.data.dto.profileImage


import com.example.assu_fe_app.domain.model.profileImage.ProfileImageModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileImageResponseDto(
    val key: String
) {
    fun toModel() = ProfileImageModel(
        key = key
    )
}