package com.example.assu_fe_app.data.dto.certification

data class UserSessionRequestDto(
    val adminId: Long,
    val people: Int,
    val storeId: Long,
    val tableNumber: Int
)