package com.example.assu_fe_app.presentation.common.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.data.local.DeviceTokenLocalStore
import com.example.assu_fe_app.domain.usecase.deviceToken.UnregisterDeviceTokenUseCase
import com.example.assu_fe_app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MypageViewModel @Inject constructor(
    private val unregisterDeviceToken: UnregisterDeviceTokenUseCase,
    private val localStore: DeviceTokenLocalStore,
) : ViewModel() {

    sealed interface LogoutState {
        data object Idle : LogoutState
        data object Unregistering : LogoutState
        data object Done : LogoutState
        data class Error(val msg: String) : LogoutState
    }

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState

    fun logoutAndUnregister() = viewModelScope.launch {
        _logoutState.value = LogoutState.Unregistering

        val tokenId = localStore.getTokenId()
        if (tokenId != null) {
            when (val r = unregisterDeviceToken(tokenId)) {
                is RetrofitResult.Success -> {
                    // no-op
                }
                is RetrofitResult.Fail -> {
                    // 로깅/모니터링 정도만 하고 UX는 진행
                }
                is RetrofitResult.Error -> {
                    // 네트워크 에러 등
                }
            }
        }
        // 로컬 정리(성공/실패 무관)
        localStore.clearTokenId()

        _logoutState.value = LogoutState.Done
    }
}