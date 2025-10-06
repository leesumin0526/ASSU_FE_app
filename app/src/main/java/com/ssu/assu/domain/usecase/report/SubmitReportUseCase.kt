package com.ssu.assu.domain.usecase.report

import com.ssu.assu.data.dto.report.ReportResponseDTO
import com.ssu.assu.data.repository.report.ReportRepository
import com.ssu.assu.domain.model.report.ReportTargetType
import com.ssu.assu.domain.model.report.ReportType
import com.ssu.assu.util.RetrofitResult
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