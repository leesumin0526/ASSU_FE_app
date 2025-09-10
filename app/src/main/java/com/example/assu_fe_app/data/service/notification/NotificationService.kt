package com.example.assu_fe_app.data.service.notification

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.notification.response.NotificationSettingsResponseDto
import com.example.assu_fe_app.data.dto.notification.response.NotificationsPageDto
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface NotificationService {
    @PUT("/notifications/{type}")
    suspend fun toggle(
        @Path("type") type: String
    ): BaseResponse<NotificationSettingsResponseDto>

    @GET("/notifications/settings")
    suspend fun getSettings(): BaseResponse<NotificationSettingsResponseDto>

    @GET("/notifications")
    suspend fun getNotifications(
        @Query("status") status: String = "all",  // "all" | "unread"
        @Query("page") page: Int = 1,            // 1-based
        @Query("size") size: Int = 20
    ): BaseResponse<NotificationsPageDto>

    @POST("/notifications/{notification-id}/read")
    suspend fun markRead(
        @Path("notification-id") notificationId: Long
    ): BaseResponse<String>

    @GET("notifications/unread-exists")
    suspend fun unreadExists(): BaseResponse<Boolean>
}