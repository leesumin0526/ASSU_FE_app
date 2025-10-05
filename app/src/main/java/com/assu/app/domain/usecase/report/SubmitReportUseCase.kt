package com.assu.app.domain.usecase.report

import com.assu.app.data.dto.report.ReportResponseDTO
import com.assu.app.data.repository.report.ReportRepository
import com.assu.app.domain.model.report.ReportTargetType
import com.assu.app.domain.model.report.ReportType
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class SubmitReportUseCase @Inject constructor(

    private val repo: ReportRepository
) {
    suspend fun submitContentReport(
        targetType: ReportTargetType,
        targetId: Long,
        reportType: ReportType
    ): RetrofitResult<ReportResponseDTO> =
        repo.submitContentReport(targetType, targetId, reportType)

    suspend fun submitStudentReport(
        targetType: ReportTargetType,
        targetId: Long,
        reportType: ReportType
    ): RetrofitResult<ReportResponseDTO> =
        repo.submitStudentReport(targetType, targetId, reportType)
}