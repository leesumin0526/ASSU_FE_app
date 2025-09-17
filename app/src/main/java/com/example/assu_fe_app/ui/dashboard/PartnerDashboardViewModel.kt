package com.example.assu_fe_app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.data.dto.dashboard.PartnerDashboardItem.RankingChartItem
import com.example.assu_fe_app.domain.model.dashboard.PartnerDashboardModel
import com.example.assu_fe_app.domain.model.dashboard.StoreInfoModel
import com.example.assu_fe_app.domain.usecase.dashboard.GetPartnerWeeklyRankListUseCase
import com.example.assu_fe_app.domain.usecase.dashboard.GetPartnerWeeklyRankUseCase
import com.example.assu_fe_app.domain.usecase.dashboard.GetTodayBestStoreUseCase
import com.example.assu_fe_app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PartnerDashboardViewModel @Inject constructor(
    private val getTodayBestStoreUseCase: GetTodayBestStoreUseCase,
    private val getPartnerWeeklyRankUseCase: GetPartnerWeeklyRankUseCase,
    private val getPartnerWeeklyRankListUseCase: GetPartnerWeeklyRankListUseCase
) : ViewModel() {

    sealed interface DashboardUiState {
        data object Idle : DashboardUiState
        data object Loading : DashboardUiState
        data class Success(val data: PartnerDashboardModel) : DashboardUiState
        data class Fail(val code: Int, val message: String?) : DashboardUiState
        data class Error(val message: String) : DashboardUiState
    }

    private val _dashboardState = MutableStateFlow<DashboardUiState>(DashboardUiState.Idle)
    val dashboardState: StateFlow<DashboardUiState> = _dashboardState

    private val _selectedWeekIndex = MutableStateFlow(5) // 최근 주차 (0-based index)
    val selectedWeekIndex: StateFlow<Int> = _selectedWeekIndex

    init {
        loadPartnerDashboard()
    }

    fun loadPartnerDashboard() {
        viewModelScope.launch {
            _dashboardState.value = DashboardUiState.Loading

            try {
                coroutineScope {
                    val todayBestDeferred = async { getTodayBestStoreUseCase() }
                    val weeklyRankDeferred = async { getPartnerWeeklyRankUseCase() }
                    val weeklyRankListDeferred = async { getPartnerWeeklyRankListUseCase() }

                    val todayBestResult = todayBestDeferred.await()
                    val weeklyRankResult = weeklyRankDeferred.await()
                    val weeklyRankListResult = weeklyRankListDeferred.await()

                    // 모든 결과가 성공인지 확인
                    if (todayBestResult is RetrofitResult.Success &&
                        weeklyRankResult is RetrofitResult.Success &&
                        weeklyRankListResult is RetrofitResult.Success) {

                        val partnerDashboardModel = PartnerDashboardModel(
                            storeInfo = StoreInfoModel(
                                storeId = getCurrentStoreId(),
                                storeName = getCurrentStoreName()
                            ),
                            weeklyRanks = weeklyRankListResult.data.map { it.toModel() },
                            todayBest = todayBestResult.data.toPopularStoreModels(),
                            adminStats = null // Partner는 Admin 통계 없음
                        )

                        // 가장 최근 주차로 선택 초기화
                        _selectedWeekIndex.value = partnerDashboardModel.weeklyRanks.size - 1

                        _dashboardState.value = DashboardUiState.Success(partnerDashboardModel)
                    } else {
                        // 첫 번째 실패 결과 처리
                        val failResult = listOf(todayBestResult, weeklyRankResult, weeklyRankListResult)
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

    fun selectWeek(weekIndex: Int) {
        val currentData = getCurrentData()
        if (weekIndex >= 0 && weekIndex < (currentData?.weeklyRanks?.size ?: 0)) {
            _selectedWeekIndex.value = weekIndex
        }
    }

    fun getSelectedWeekUsage(): Long {
        val weeklyRanks = getCurrentData()?.weeklyRanks
        return weeklyRanks?.getOrNull(_selectedWeekIndex.value)?.usageCount ?: 0L
    }

    fun getSelectedWeekRank(): Long {
        val weeklyRanks = getCurrentData()?.weeklyRanks
        return weeklyRanks?.getOrNull(_selectedWeekIndex.value)?.rank ?: 0L
    }

    fun getAnalysisText(): String {
        return getCurrentData()?.getAnalysisText(_selectedWeekIndex.value)
            ?: "데이터를 불러오는 중입니다."
    }

    fun getRankingChartItems(): List<RankingChartItem> {
        val weeklyRanks = getCurrentData()?.weeklyRanks ?: return emptyList()

        return weeklyRanks.mapIndexed { index, rankModel ->
            val isImprovement = if (index > 0) {
                rankModel.rank < weeklyRanks[index - 1].rank
            } else false

            val isDecline = if (index > 0) {
                rankModel.rank > weeklyRanks[index - 1].rank
            } else false

            RankingChartItem(
                weekIndex = index,
                rank = rankModel.rank,
                isImprovement = isImprovement,
                isDecline = isDecline
            )
        }
    }

    fun refreshDashboard() {
        loadPartnerDashboard()
    }

    fun resetState() {
        _dashboardState.value = DashboardUiState.Idle
    }

    // Helper functions
    private fun getCurrentData(): PartnerDashboardModel? {
        return when (val state = _dashboardState.value) {
            is DashboardUiState.Success -> state.data
            else -> null
        }
    }

    private fun getCurrentStoreId(): Long {
        // SharedPreferences나 DataStore에서 현재 스토어 ID 가져오기
        return 1L // 임시값
    }

    private fun getCurrentStoreName(): String {
        // 캐시된 스토어 이름 가져오기
        return "역전할머니맥주 숭실대점" // 임시값
    }
}

// Chart Item 데이터 클래스들
data class RankingChartItem(
    val weekIndex: Int,
    val rank: Long,
    val isImprovement: Boolean = false,
    val isDecline: Boolean = false
)