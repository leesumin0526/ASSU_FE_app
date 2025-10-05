package com.assu.app.data.dto.notification.response

import com.assu.app.domain.model.notification.NotificationsPageModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationsPageDto(
    val items: List<NotificationResponseDto>,
    val page: Int,
    val size: Int,
    val totalPages: Int,
    val totalElements: Long
){
    fun toModel() = NotificationsPageModel(
        items = items.map { it.toModel() },
        page = page,
        size = size,
        totalPages = totalPages,
        totalElements = totalElements
    )
}