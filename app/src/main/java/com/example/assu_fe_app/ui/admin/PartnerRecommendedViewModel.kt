package com.example.assu_fe_app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.domain.model.admin.RecommendedPartnerModel
import com.example.assu_fe_app.domain.usecase.admin.GetRecommendedPartnerUseCase
import com.example.assu_fe_app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PartnerRecommendViewModel @Inject constructor(
    private val getRecommendedPartnerUseCase: GetRecommendedPartnerUseCase
) : ViewModel() {

    sealed interface RecommendUiState {
        data object Idle : RecommendUiState
        data object Loading : RecommendUiState
        data class Success(val partner: RecommendedPartnerModel) : RecommendUiState
        data class Error(val message: String) : RecommendUiState
    }

    private val _recommendState = MutableStateFlow<RecommendUiState>(RecommendUiState.Idle)
    val recommendState: StateFlow<RecommendUiState> = _recommendState

    init {
        loadRecommendedPartner()
    }

    fun loadRecommendedPartner() {
        viewModelScope.launch {
            _recommendState.value = RecommendUiState.Loading

            when (val result = getRecommendedPartnerUseCase()) {
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

    fun refreshPartner() {
        loadRecommendedPartner()
    }
}