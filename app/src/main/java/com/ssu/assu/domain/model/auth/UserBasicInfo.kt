package com.ssu.assu.domain.model.auth

data class UserBasicInfo(
    val name: String,
    val university: String?,
    val department: String?,
    val major: String?
)
