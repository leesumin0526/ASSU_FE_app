package com.ssu.assu.data.repositoryImpl.report

import com.ssu.assu.data.dto.report.ContentReportRequestDTO
import com.ssu.assu.data.dto.report.ReportResponseDTO
import com.ssu.assu.data.dto.report.StudentReportRequestDTO
import com.ssu.assu.data.repository.report.ReportRepository
import com.ssu.assu.data.service.report.ReportService
import com.ssu.assu.domain.model.report.ReportTargetType
import com.ssu.assu.domain.model.report.ReportType
import com.ssu.assu.util.RetrofitResult
import com.ssu.assu.util.apiHandler
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