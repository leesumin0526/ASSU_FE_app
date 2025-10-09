package com.ssu.assu.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssu.assu.domain.model.dashboard.AdminDashboardModel
import com.ssu.assu.domain.usecase.dashboard.GetDetailedUsageListUseCase
import com.ssu.assu.domain.usecase.dashboard.GetNewStudentCountUseCase
import com.ssu.assu.domain.usecase.dashboard.GetTodayUsageCountUseCase
import com.ssu.assu.domain.usecase.dashboard.GetTotalStudentCountUseCase
import com.ssu.assu.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val getTotalStudentCountUseCase: GetTotalStudentCountUseCase,
    private val getNewStudentCountUseCase: GetNewStudentCountUseCase,
    private val getTodayUsageCountUseCase: GetTodayUsageCountUseCase,
    private val getDetailedUsageListUseCase: GetDetailedUsageListUseCase
) : ViewModel() {

    sealed interface DashboardUiState {
        data object Idle : DashboardUiState
        data object Loading : DashboardUiState
        data class Success(val data: AdminDashboardModel) : DashboardUiState
        data class Fail(val code: Int, val message: String?) : DashboardUiState
        data class Error(val message: String) : DashboardUiState
    }

    private val _dashboardState = MutableStateFlow<DashboardUiState>(DashboardUiState.Idle)
    val dashboardState: StateFlow<DashboardUiState> = _dashboardState

    fun loadAdminDashboard() {
        viewModelScope.launch {
            _dashboardState.value = DashboardUiState.Loading

            try {
                coroutineScope {
                    // 병렬로 5개 UseCase 호출
                    val totalStudentsDeferred = async { getTotalStudentCountUseCase() }
                    val newStudentsDeferred = async { getNewStudentCountUseCase() }
                    val todayUsageDeferred = async { getTodayUsageCountUseCase() }
                    val detailedUsageDeferred = async { getDetailedUsageListUseCase() }

                    val totalStudentsResult = totalStudentsDeferred.await()
                    val newStudentsResult = newStudentsDeferred.await()
                    val todayUsageResult = todayUsageDeferred.await()
                    val detailedUsageResult = detailedUsageDeferred.await()

                    // 모든 결과가 성공인지 확인
                    if (totalStudentsResult is RetrofitResult.Success &&
                        newStudentsResult is RetrofitResult.Success &&
                        todayUsageResult is RetrofitResult.Success &&
                        detailedUsageResult is RetrofitResult.Success) {

                        val adminDashboardModel = AdminDashboardModel(
                            totalStudentCount = totalStudentsResult.data.toInt(),
                            newStudentCount = newStudentsResult.data.toInt(),
                            todayUsagePersonCount = todayUsageResult.data.toInt(),
                            storeUsageStats = detailedUsageResult.data,
                            monthlyUsageCount = 0
                        )

                        _dashboardState.value = DashboardUiState.Success(adminDashboardModel)
                    } else {
                        // 첫 번째 실패 결과 처리
                        val failResult = listOf(
                            totalStudentsResult,
                            newStudentsResult,
                            todayUsageResult,
                            detailedUsageResult
                        ).firstOrNull { it is RetrofitResult.Fail } as? RetrofitResult.Fail

                        if (failResult != null) {
                            _dashboardState.value = DashboardUiState.Fail(failResult.statusCode, failResult.message)
                        } else {
                            _dashboardState.value = DashboardUiState.Error("네트워크 오류가 발생했습니다")
                        }
                    }
                }
            } catch (e: Exception) {
                _dashboardState.value = DashboardUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun refreshDashboard() {
        loadAdminDashboard()
    }

    fun resetState() {
        _dashboardState.value = DashboardUiState.Idle
    }
}