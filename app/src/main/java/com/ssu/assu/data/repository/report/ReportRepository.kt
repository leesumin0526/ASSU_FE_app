package com.ssu.assu.data.repository.report

import com.ssu.assu.data.dto.report.ReportResponseDTO
import com.ssu.assu.domain.model.report.ReportTargetType
import com.ssu.assu.domain.model.report.ReportType
import com.ssu.assu.util.RetrofitResult

interface ReportRepository {
    /**
     * 콘텐츠(리뷰/건의글) 신고
     * @param targetType 신고 대상 타입
     * @param targetId 신고 대상 ID
     * @param reportType 신고 유형
     * @return 신고 결과
     */
    suspend fun submitContentReport(
        targetType: ReportTargetType,
        targetId: Long,
        reportType: ReportType
    ): RetrofitResult<ReportResponseDTO>

    /**
     * 작성자(학생) 신고
     */
    suspend fun submitStudentReport(
        targetType: ReportTargetType,
        targetId: Long,
        reportType: ReportType
    ): RetrofitResult<ReportResponseDTO>
}