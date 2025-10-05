package com.assu.app.domain.model.auth

data class UserBasicInfo(
    val name: String,
    val university: String?,
    val department: String?,
    val major: String?
)
