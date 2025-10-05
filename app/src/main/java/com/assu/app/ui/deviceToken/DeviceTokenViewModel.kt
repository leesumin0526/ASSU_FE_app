package com.assu.app.ui.deviceToken

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assu.app.data.local.DeviceTokenLocalStore
import com.assu.app.domain.usecase.deviceToken.RegisterDeviceTokenUseCase
import com.assu.app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceTokenViewModel @Inject constructor(
    private val registerDeviceToken: RegisterDeviceTokenUseCase,
    private val localStore: DeviceTokenLocalStore
) : ViewModel() {

    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
        data class Success(val tokenId: Long) : UiState   // ← String → Long
        data class Fail(val code: Int, val msg: String) : UiState
        data class Error(val msg: String) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun register(token: String) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            when (val r = registerDeviceToken(token)) {
                is RetrofitResult.Success -> {
                    val tokenId = r.data
                    localStore.saveDeviceTokenId(tokenId)
                    _uiState.value = UiState.Success(tokenId)
                }
                is RetrofitResult.Fail ->
                    _uiState.value = UiState.Fail(r.statusCode, r.message)
                is RetrofitResult.Error ->
                    _uiState.value = UiState.Error(r.exception.message ?: "unknown error")
            }
        }
    }
}