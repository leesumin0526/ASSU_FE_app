package com.assu.app.data.dto.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StudentTokenVerifyResponseDto(
    val studentNumber: String,
    val name: String,
    val enrollmentStatus: String?, // nullable로 변경
    val yearSemester: String,
    val major: String
)
