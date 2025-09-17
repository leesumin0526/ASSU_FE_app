package com.example.assu_fe_app.data.dto.certification.response

data class CertificationProgressDto(
    val type: String,        // "progress" 또는 "completed"
    val count: Int,          // 현재 인증된 인원수 (항상 포함)
    val message: String?,    // 완료 시에만 포함
    val userIds: List<Long>? // 완료 시에만 포함
)