package com.ssu.assu.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssu.assu.domain.model.report.ReportResult
import com.ssu.assu.domain.model.report.ReportTargetType
import com.ssu.assu.domain.model.report.ReportType
import com.ssu.assu.domain.usecase.report.SubmitReportUseCase
import com.ssu.assu.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val submitReportUseCase: SubmitReportUseCase
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _reportId = MutableLiveData<Long?>()
    val reportId: LiveData<Long?> = _reportId

    private val _reportCompleteEvent = MutableLiveData<ReportResult>()
    val reportCompleteEvent: LiveData<ReportResult> = _reportCompleteEvent

    /**
     * 신고 제출
     */
    fun submitReport(
        targetType: ReportTargetType,
        targetId: Long,
        reportType: ReportType,
        isStudentReport: Boolean
    ) {
        // 간단한 유효성 검증
        if (targetId <= 0) {
            _isLoading.value = false
            _isSuccess.value = false
            _error.value = "잘못된 대상 ID입니다."
            _reportId.value = null
            return
        }

        _isLoading.value = true
        _isSuccess.value = false
        _error.value = null
        _reportId.value = null

        viewModelScope.launch {
            try {
                val result = if (isStudentReport) {
                    submitReportUseCase.submitStudentReport(targetType, targetId, reportType)
                } else {
                    submitReportUseCase.submitContentReport(targetType, targetId, reportType)
                }

                _isLoading.value = false

                when (result) {
                    is RetrofitResult.Success -> {
                        _isSuccess.value = true
                        _reportId.value = result.data.reportId
                        _error.value = null
                        _reportCompleteEvent.value = ReportResult(
                            isSuccess = true,
                            reportId = result.data.reportId,
                            message = "신고가 접수되었습니다."
                        )
                    }
                    is RetrofitResult.Fail -> {
                        _isSuccess.value = false
                        _reportId.value = null
                        _error.value = result.message
                    }
                    is RetrofitResult.Error -> {
                        _isSuccess.value = false
                        _reportId.value = null
                        _error.value = result.exception.message ?: "알 수 없는 오류가 발생했습니다."
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _isSuccess.value = false
                _reportId.value = null
                _error.value = e.message ?: "알 수 없는 오류가 발생했습니다."
            }
        }
    }

    /**
     * 콘텐츠 신고
     */
    fun reportContent(
        targetType: ReportTargetType,
        targetId: Long,
        reportType: ReportType
    ) {
        submitReport(targetType, targetId, reportType, isStudentReport = false)
    }

    /**
     * 작성자 신고
     */
    fun reportStudent(
        targetType: ReportTargetType,
        targetId: Long,
        reportType: ReportType
    ) {
        submitReport(targetType, targetId, reportType, isStudentReport = true)
    }

    /**
     * 사용 가능한 신고 유형 목록 조회
     */
    fun getAvailableReportTypes(
        targetType: ReportTargetType,
        isStudentReport: Boolean
    ): List<ReportType> {
        return if (isStudentReport) {
            // 작성자(학생) 신고 가능한 유형
            listOf(
                ReportType.STUDENT_USER_SPAM,
                ReportType.STUDENT_USER_INAPPROPRIATE_CONTENT,
                ReportType.STUDENT_USER_HARASSMENT,
                ReportType.STUDENT_USER_FRAUD,
                ReportType.STUDENT_USER_PRIVACY_VIOLATION,
                ReportType.STUDENT_USER_OTHER
            )
        } else {
            // 콘텐츠 신고 가능한 유형
            when (targetType) {
                ReportTargetType.REVIEW -> listOf(
                    ReportType.REVIEW_INAPPROPRIATE_CONTENT,
                    ReportType.REVIEW_FALSE_INFORMATION,
                    ReportType.REVIEW_SPAM
                )
                ReportTargetType.SUGGESTION -> listOf(
                    ReportType.SUGGESTION_INAPPROPRIATE_CONTENT,
                    ReportType.SUGGESTION_FALSE_INFORMATION,
                    ReportType.SUGGESTION_SPAM
                )
                ReportTargetType.STUDENT_USER -> {
                    // STUDENT_USER는 작성자 신고로만 사용되어야 함
                    emptyList()
                }
            }
        }
    }

    /**
     * 상태 초기화
     */
    fun reset() {
        _isLoading.value = false
        _isSuccess.value = false
        _error.value = null
        _reportId.value = null
    }

    /**
     * 에러 메시지 소비
     */
    fun clearError() {
        _error.value = null
    }
}