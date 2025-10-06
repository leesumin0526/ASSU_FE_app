package com.ssu.assu.data.dto.certification.request

data class UserSessionRequestDto(
    val adminId: Long,
    val people: Int,
    val storeId: Long,
    val tableNumber: Int
)