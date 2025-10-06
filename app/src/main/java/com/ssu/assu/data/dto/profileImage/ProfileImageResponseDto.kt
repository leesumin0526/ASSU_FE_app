package com.ssu.assu.data.dto.profileImage


import com.ssu.assu.domain.model.profileImage.ProfileImageModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileImageResponseDto(
    val key: String
) {
    fun toModel() = ProfileImageModel(
        key = key
    )
}