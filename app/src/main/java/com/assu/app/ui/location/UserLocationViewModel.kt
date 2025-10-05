package com.assu.app.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assu.app.data.dto.location.ViewportQuery
import com.assu.app.domain.model.location.StoreOnMap
import com.assu.app.domain.usecase.location.GetNearbyStoresUseCase
import com.assu.app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class UserLocationViewModel @Inject constructor(
    private val getNearbyStores: GetNearbyStoresUseCase
) : ViewModel() {

    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
        data class Success(val items: List<StoreOnMap>) : UiState
        data class Fail(val code: String?, val message: String?) : UiState
        data class Error(val t: Throwable) : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state: StateFlow<UiState> = _state

    fun load(v: ViewportQuery) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            when (val res = getNearbyStores(v)) {
                is RetrofitResult.Success -> _state.value = UiState.Success(res.data)
                is RetrofitResult.Fail -> _state.value = UiState.Fail(res.statusCode.toString(), res.message)
                is RetrofitResult.Error -> _state.value = UiState.Error(res.exception)
            }
        }
    }
}