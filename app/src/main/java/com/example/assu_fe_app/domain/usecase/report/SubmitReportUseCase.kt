package com.example.assu_fe_app.domain.usecase.report

import com.example.assu_fe_app.data.dto.report.ReportResponseDTO
import com.example.assu_fe_app.data.repository.report.ReportRepository
import com.example.assu_fe_app.domain.model.report.ReportResult
import com.example.assu_fe_app.domain.model.report.ReportTargetType
import com.example.assu_fe_app.domain.model.report.ReportType
import com.example.assu_fe_app.util.RetrofitResult
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