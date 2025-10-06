package com.ssu.assu.data.dto.report

import com.google.gson.annotations.SerializedName

data class ContentReportRequestDTO(
    @SerializedName("targetType")
    val targetType: String,

    @SerializedName("targetId")
    val targetId: Long,

    @SerializedName("reportType")
    val reportType: String
)

/**
 * 작성자 신고 요청 DTO
 */
data class StudentReportRequestDTO(
    @SerializedName("targetType")
    val targetType: String,

    @SerializedName("targetId")
    val targetId: Long,

    @SerializedName("reportType")
    val reportType: String
)

/**
 * 신고 응답 DTO (BaseResponse의 result 부분)
 */
data class ReportResponseDTO(
    @SerializedName("reportId")
    val reportId: Long
)