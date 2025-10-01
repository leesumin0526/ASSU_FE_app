package com.example.assu_fe_app.domain.model.report

enum class ReportTargetType(val apiValue: String) {
    STUDENT_USER("STUDENT_USER"),
    REVIEW("REVIEW"),
    SUGGESTION("SUGGESTION")
}

/**
 * 신고 유형
 */
enum class ReportType(val apiValue: String) {
    // 사용자 신고용
    STUDENT_USER_SPAM("STUDENT_USER_SPAM"),
    STUDENT_USER_INAPPROPRIATE_CONTENT("STUDENT_USER_INAPPROPRIATE_CONTENT"),
    STUDENT_USER_HARASSMENT("STUDENT_USER_HARASSMENT"),
    STUDENT_USER_FRAUD("STUDENT_USER_FRAUD"),
    STUDENT_USER_PRIVACY_VIOLATION("STUDENT_USER_PRIVACY_VIOLATION"),
    STUDENT_USER_OTHER("STUDENT_USER_OTHER"),

    // 리뷰 신고용
    REVIEW_INAPPROPRIATE_CONTENT("REVIEW_INAPPROPRIATE_CONTENT"),
    REVIEW_FALSE_INFORMATION("REVIEW_FALSE_INFORMATION"),
    REVIEW_SPAM("REVIEW_SPAM"),

    // 건의글 신고용
    SUGGESTION_INAPPROPRIATE_CONTENT("SUGGESTION_INAPPROPRIATE_CONTENT"),
    SUGGESTION_FALSE_INFORMATION("SUGGESTION_FALSE_INFORMATION"),
    SUGGESTION_SPAM("SUGGESTION_SPAM");

    companion object {
        fun fromApiValue(value: String): ReportType? {
            return values().find { it.apiValue == value }
        }
    }
}

/**
 * 콘텐츠 신고 요청
 */
data class ContentReportRequest(
    val targetType: String,
    val targetId: Long,
    val reportType: String
)

/**
 * 작성자 신고 요청
 */
data class StudentReportRequest(
    val targetType: String,
    val targetId: Long,
    val reportType: String
)

/**
 * 신고 응답
 */
data class ReportResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ReportResponseResult
)

/**
 * 신고 응답 결과
 */
data class ReportResponseResult(
    val reportId: Long
)

/**
 * 신고 결과 (도메인 모델)
 */
data class ReportResult(
    val isSuccess: Boolean,
    val reportId: Long? = null,
    val message: String
)