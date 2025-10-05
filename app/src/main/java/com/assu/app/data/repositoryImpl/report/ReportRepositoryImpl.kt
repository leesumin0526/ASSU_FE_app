package com.assu.app.data.repositoryImpl.report

import com.assu.app.data.dto.report.ContentReportRequestDTO
import com.assu.app.data.dto.report.ReportResponseDTO
import com.assu.app.data.dto.report.StudentReportRequestDTO
import com.assu.app.data.repository.report.ReportRepository
import com.assu.app.data.service.report.ReportService
import com.assu.app.domain.model.report.ReportTargetType
import com.assu.app.domain.model.report.ReportType
import com.assu.app.util.RetrofitResult
import com.assu.app.util.apiHandler
import jakarta.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val api: ReportService
) : ReportRepository {

    override suspend fun submitContentReport(
        targetType: ReportTargetType,
        targetId: Long,
        reportType: ReportType
    ): RetrofitResult<ReportResponseDTO> {
        val requestDTO = ContentReportRequestDTO(
            targetType = targetType.apiValue,
            targetId = targetId,
            reportType = reportType.apiValue
        )

        return try {
            apiHandler(
                { api.submitContentReport(requestDTO) },
                { dto -> dto }
            )
        } catch (e: Exception) {
            RetrofitResult.Error(e)
        }
    }

    override suspend fun submitStudentReport(
        targetType: ReportTargetType,
        targetId: Long,
        reportType: ReportType
    ): RetrofitResult<ReportResponseDTO> {
        val requestDTO = StudentReportRequestDTO(
            targetType = targetType.apiValue,
            targetId = targetId,
            reportType = reportType.apiValue
        )

        return try {
            apiHandler(
                { api.submitStudentReport(requestDTO) },
                { dto -> dto }
            )
        } catch (e: Exception) {
            RetrofitResult.Error(e)
        }
    }
}