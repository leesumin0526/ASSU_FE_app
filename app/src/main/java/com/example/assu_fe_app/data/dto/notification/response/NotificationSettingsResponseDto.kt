package com.example.assu_fe_app.data.dto.notification.response

@kotlinx.serialization.Serializable
data class NotificationSettingsResponseDto(
    val settings: Map<String, Boolean> = emptyMap()
)
