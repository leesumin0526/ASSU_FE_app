package com.assu.app.data.dto.profileImage


import com.assu.app.domain.model.profileImage.ProfileImageModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileImageResponseDto(
    val key: String
) {
    fun toModel() = ProfileImageModel(
        key = key
    )
}