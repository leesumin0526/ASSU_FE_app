package com.example.assu_fe_app.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.domain.usecase.user.GetStampUseCase
import com.example.assu_fe_app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserHomeViewModel @Inject constructor(
    private val getStampCountUseCase: GetStampUseCase
) : ViewModel() {

    sealed interface StampUiState {
        data object Idle : StampUiState
        data object Loading : StampUiState
        data class Success(val stampCount: Int) : StampUiState
        data class Error(val message: String) : StampUiState
    }

    private val _stampState = MutableStateFlow<StampUiState>(StampUiState.Idle)
    val stampState: StateFlow<StampUiState> = _stampState

    init {
        loadStampCount()
    }

    fun loadStampCount() {
        viewModelScope.launch {
            _stampState.value = StampUiState.Loading

            when (val result = getStampCountUseCase()) {
                is RetrofitResult.Success -> {
                    _stampState.value = StampUiState.Success(result.data)
                }
                is RetrofitResult.Fail -> {
                    _stampState.value = StampUiState.Error("서버 오류: ${result.message}")
                }
                is RetrofitResult.Error -> {
                    _stampState.value = StampUiState.Error("네트워크 오류가 발생했습니다")
                }
            }
        }
    }

    fun refreshStamp() {
        loadStampCount()
    }
}