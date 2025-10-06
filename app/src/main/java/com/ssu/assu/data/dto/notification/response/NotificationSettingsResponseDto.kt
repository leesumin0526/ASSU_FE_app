package com.ssu.assu.data.dto.notification.response

@kotlinx.serialization.Serializable
data class NotificationSettingsResponseDto(
    val settings: Map<String, Boolean> = emptyMap()
)
