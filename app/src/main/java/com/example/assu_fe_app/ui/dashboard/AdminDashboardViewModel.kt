package com.example.assu_fe_app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.domain.model.dashboard.AdminDashboardModel
import com.example.assu_fe_app.domain.usecase.dashboard.GetTotalStudentCountUseCase
import com.example.assu_fe_app.domain.usecase.dashboard.GetNewStudentCountUseCase
import com.example.assu_fe_app.domain.usecase.dashboard.GetTodayUsageCountUseCase
import com.example.assu_fe_app.domain.usecase.dashboard.GetStoreUsageListUseCase
import com.example.assu_fe_app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val getTotalStudentCountUseCase: GetTotalStudentCountUseCase,
    private val getNewStudentCountUseCase: GetNewStudentCountUseCase,
    private val getTodayUsageCountUseCase: GetTodayUsageCountUseCase,
    private val getStoreUsageListUseCase: GetStoreUsageListUseCase
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

    init {
        loadAdminDashboard()
    }

    fun loadAdminDashboard() {
        viewModelScope.launch {
            _dashboardState.value = DashboardUiState.Loading

            try {
                coroutineScope {
                    val totalStudentDeferred = async { getTotalStudentCountUseCase() }
                    val newStudentDeferred = async { getNewStudentCountUseCase() }
                    val todayUsageDeferred = async { getTodayUsageCountUseCase() }
                    val storeUsageDeferred = async { getStoreUsageListUseCase() }

                    val totalStudentResult = totalStudentDeferred.await()
                    val newStudentResult = newStudentDeferred.await()
                    val todayUsageResult = todayUsageDeferred.await()
                    val storeUsageResult = storeUsageDeferred.await()

                    // 모든 결과가 성공인지 확인
                    if (totalStudentResult is RetrofitResult.Success &&
                        newStudentResult is RetrofitResult.Success &&
                        todayUsageResult is RetrofitResult.Success &&
                        storeUsageResult is RetrofitResult.Success) {

                        val adminDashboardModel = AdminDashboardModel(
                            adminId = 0L, // TODO: 실제 adminId 가져오기
                            adminName = "관리자", // TODO: 실제 adminName 가져오기
                            totalStudentCount = totalStudentResult.data,
                            newStudentCount = newStudentResult.data,
                            todayUsagePersonCount = todayUsageResult.data,
                            storeUsageStats = storeUsageResult.data.map {
                                com.example.assu_fe_app.domain.model.dashboard.StoreUsageModel(
                                    storeId = it.storeId,
                                    storeName = it.storeName,
                                    usageCount = it.usageCount
                                )
                            }
                        )

                        _dashboardState.value = DashboardUiState.Success(adminDashboardModel)
                    } else {
                        // 첫 번째 실패 결과 처리
                        val failResult = listOf(totalStudentResult, newStudentResult, todayUsageResult, storeUsageResult)
                            .firstOrNull { it is RetrofitResult.Fail } as? RetrofitResult.Fail

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

    // Helper functions for UI
    fun getCurrentData(): AdminDashboardModel? {
        return when (val state = _dashboardState.value) {
            is DashboardUiState.Success -> state.data
            else -> null
        }
    }
}