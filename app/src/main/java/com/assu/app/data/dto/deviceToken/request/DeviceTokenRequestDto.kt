package com.assu.app.data.dto.deviceToken.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeviceTokenRequestDto(
    val token: String
)
