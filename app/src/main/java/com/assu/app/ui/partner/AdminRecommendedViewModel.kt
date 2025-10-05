package com.assu.app.ui.partner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assu.app.domain.model.partner.RecommendedAdminModel
import com.assu.app.domain.usecase.partner.GetRecommendedAdminsUseCase
import com.assu.app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AdminRecommendViewModel @Inject constructor(
    private val getRecommendedAdminsUseCase: GetRecommendedAdminsUseCase
) : ViewModel() {

    sealed interface RecommendUiState {
        data object Idle : RecommendUiState
        data object Loading : RecommendUiState
        data class Success(val admins: List<RecommendedAdminModel>) : RecommendUiState
        data class Error(val message: String) : RecommendUiState
    }

    private val _recommendState = MutableStateFlow<RecommendUiState>(RecommendUiState.Idle)
    val recommendState: StateFlow<RecommendUiState> = _recommendState

    init {
        loadRecommendedAdmins()
    }

    fun loadRecommendedAdmins() {
        viewModelScope.launch {
            _recommendState.value = RecommendUiState.Loading

            when (val result = getRecommendedAdminsUseCase()) {
                is RetrofitResult.Success -> {
                    _recommendState.value = RecommendUiState.Success(result.data)
                }
                is RetrofitResult.Fail -> {
                    _recommendState.value = RecommendUiState.Error("서버 오류: ${result.message}")
                }
                is RetrofitResult.Error -> {
                    _recommendState.value = RecommendUiState.Error("네트워크 오류가 발생했습니다")
                }
            }
        }
    }

    fun refreshAdmins() {
        loadRecommendedAdmins()
    }
}