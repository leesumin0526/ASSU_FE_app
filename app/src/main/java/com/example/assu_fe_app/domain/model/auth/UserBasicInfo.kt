package com.example.assu_fe_app.domain.model.auth

data class UserBasicInfo(
    val name: String,
    val university: String?,
    val department: String?,
    val major: String?
)
