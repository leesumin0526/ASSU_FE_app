package com.example.assu_fe_app.data.repository.report

import com.example.assu_fe_app.data.dto.report.ReportResponseDTO
import com.example.assu_fe_app.domain.model.report.ReportTargetType
import com.example.assu_fe_app.domain.model.report.ReportType
import com.example.assu_fe_app.util.RetrofitResult

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