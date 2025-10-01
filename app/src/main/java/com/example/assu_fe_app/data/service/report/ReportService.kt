package com.example.assu_fe_app.data.service.report

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.report.ContentReportRequestDTO
import com.example.assu_fe_app.data.dto.report.ReportResponseDTO
import com.example.assu_fe_app.data.dto.report.StudentReportRequestDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportService {

    /**
     * 콘텐츠(리뷰/건의글) 신고 API
     * POST /reports
     */
    @POST("reports")
    suspend fun submitContentReport(
        @Body request: ContentReportRequestDTO
    ): BaseResponse<ReportResponseDTO>

    /**
     * 작성자(학생) 신고 API
     * POST /reports/students
     */
    @POST("reports/students")
    suspend fun submitStudentReport(
        @Body request: StudentReportRequestDTO
    ): BaseResponse<ReportResponseDTO>
}