package com.ssu.assu.ui.common.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssu.assu.data.local.AuthTokenLocalStore
import com.ssu.assu.data.local.DeviceTokenLocalStore
import com.ssu.assu.domain.usecase.auth.LogoutUseCase
import com.ssu.assu.domain.usecase.auth.WithdrawUseCase
import com.ssu.assu.domain.usecase.deviceToken.UnregisterDeviceTokenUseCase
import com.ssu.assu.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MypageViewModel @Inject constructor(
    private val unregisterDeviceToken: UnregisterDeviceTokenUseCase,
    private val localStore: DeviceTokenLocalStore,
    private val logoutUseCase: LogoutUseCase,
    private val withdrawUseCase: WithdrawUseCase,
    private val authTokenLocalStore: AuthTokenLocalStore,
) : ViewModel() {

    sealed interface LogoutState {
        data object Idle : LogoutState
        data object Unregistering : LogoutState
        data object LoggingOut : LogoutState
        data object Done : LogoutState
        data class Error(val msg: String) : LogoutState
    }

    sealed interface WithdrawState {
        data object Idle : WithdrawState
        data object Unregistering : WithdrawState
        data object Withdrawing : WithdrawState
        data object Success : WithdrawState
        data class Error(val message: String) : WithdrawState
    }

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState

    private val _withdrawState = MutableStateFlow<WithdrawState>(WithdrawState.Idle)
    val withdrawState: StateFlow<WithdrawState> = _withdrawState

    fun logoutAndUnregisterFCMToken() = viewModelScope.launch {
        Log.d("MypageViewModel", "logoutAndUnregister started")
        
        // 토큰 만료 체크
        Log.d("MypageViewModel", "Checking if access token is expired...")
        val isExpired = authTokenLocalStore.isAccessTokenExpired()
        Log.d("MypageViewModel", "Token expired check result: $isExpired")
        
        if (isExpired) {
            Log.w("MypageViewModel", "Access token is expired, skipping server calls")
            // 토큰이 만료된 경우 로컬 정리만 수행
            localStore.clearDeviceTokenId()
            authTokenLocalStore.clearTokens()
            _logoutState.value = LogoutState.Done
            return@launch
        }
        
        Log.d("MypageViewModel", "Access token is valid, proceeding with server calls")
        
        _logoutState.value = LogoutState.Unregistering

        try {
            // 1. 디바이스 토큰 해제
            val tokenId = localStore.getDeviceTokenId()
            Log.d("MypageViewModel", "TokenId: $tokenId")

            if (tokenId != null) {
                Log.d("MypageViewModel", "Calling unregisterDeviceToken with tokenId: $tokenId")
                when (val r = unregisterDeviceToken(tokenId)) {
                    is RetrofitResult.Success -> {
                        Log.d("MypageViewModel", "Device token unregister success")
                    }
                    is RetrofitResult.Fail -> {
                        Log.w("MypageViewModel", "Device token unregister fail: ${r.message}")
                        // 로깅/모니터링 정도만 하고 UX는 진행
                    }
                    is RetrofitResult.Error -> {
                        Log.e("MypageViewModel", "Device token unregister error: ${r.exception.message}")
                        // 네트워크 에러 등
                    }
                }
            } else {
                Log.d("MypageViewModel", "No tokenId found, skipping unregister")
            }

            // 로컬 정리(성공/실패 무관)
            Log.d("MypageViewModel", "Clearing local tokenId")
            localStore.clearDeviceTokenId()

            // 2. 서버 로그아웃 API 호출
            Log.d("MypageViewModel", "Calling logout API")
            _logoutState.value = LogoutState.LoggingOut

            when (val result = logoutUseCase()) {
                is RetrofitResult.Success -> {
                    Log.d("MypageViewModel", "Logout API success")
                }
                is RetrofitResult.Fail -> {
                    Log.w("MypageViewModel", "Logout API fail: ${result.message}")
                }
                is RetrofitResult.Error -> {
                    Log.e("MypageViewModel", "Logout API error: ${result.exception.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("MypageViewModel", "로그아웃 중 오류 발생: ${e.message}", e)
        } finally {
            // 3. 클라이언트 토큰 삭제
            Log.d("MypageViewModel", "Clearing tokens and setting state to Done")
            authTokenLocalStore.clearTokens()
            _logoutState.value = LogoutState.Done
            Log.d("MypageViewModel", "logoutAndUnregister completed")
        }
    }

    fun withdrawAndUnregisterFCMToken() = viewModelScope.launch {
        Log.d("MypageViewModel", "withdraw() started")
        
        // 토큰 만료 체크
        Log.d("MypageViewModel", "Checking if access token is expired for withdraw...")
        val isExpired = authTokenLocalStore.isAccessTokenExpired()
        Log.d("MypageViewModel", "Token expired check result for withdraw: $isExpired")
        
        if (isExpired) {
            Log.w("MypageViewModel", "Access token is expired for withdraw, skipping server calls")
            // 토큰이 만료된 경우 로컬 정리만 수행
            localStore.clearDeviceTokenId()
            authTokenLocalStore.clearTokens()
            _withdrawState.value = WithdrawState.Success
            return@launch
        }
        
        Log.d("MypageViewModel", "Access token is valid for withdraw, proceeding with server calls")
        
        _withdrawState.value = WithdrawState.Unregistering

        try {
            // 1. 디바이스 토큰 해제
            val tokenId = localStore.getDeviceTokenId()
            Log.d("MypageViewModel", "TokenId for withdraw: $tokenId")

            if (tokenId != null) {
                Log.d("MypageViewModel", "Calling unregisterDeviceToken for withdraw with tokenId: $tokenId")
                when (val r = unregisterDeviceToken(tokenId)) {
                    is RetrofitResult.Success -> {
                        Log.d("MypageViewModel", "Device token unregister success for withdraw")
                    }
                    is RetrofitResult.Fail -> {
                        Log.w("MypageViewModel", "Device token unregister fail for withdraw: ${r.message}")
                    }
                    is RetrofitResult.Error -> {
                        Log.e("MypageViewModel", "Device token unregister error for withdraw: ${r.exception.message}")
                    }
                }
            } else {
                Log.d("MypageViewModel", "No tokenId found for withdraw, skipping unregister")
            }

            // 로컬 정리(성공/실패 무관)
            Log.d("MypageViewModel", "Clearing local tokenId for withdraw")
            localStore.clearDeviceTokenId()

            // 2. 서버 회원 탈퇴 API 호출
            Log.d("MypageViewModel", "Calling withdrawUseCase")
            _withdrawState.value = WithdrawState.Withdrawing

            when (val result = withdrawUseCase()) {
                        is RetrofitResult.Success -> {
                            Log.d("MypageViewModel", "Withdraw API success")
                            // 클라이언트 토큰 삭제
                            Log.d("MypageViewModel", "Clearing tokens after successful withdraw")
                            authTokenLocalStore.clearTokens()
                            _withdrawState.value = WithdrawState.Success
                        }
                is RetrofitResult.Fail -> {
                    Log.w("MypageViewModel", "Withdraw API fail: ${result.message}")
                    _withdrawState.value = WithdrawState.Error(result.message)
                }
                is RetrofitResult.Error -> {
                    Log.e("MypageViewModel", "Withdraw API error: ${result.exception.message}")
                    _withdrawState.value = WithdrawState.Error("네트워크 연결을 확인해주세요.")
                }
            }
        } catch (e: Exception) {
            Log.e("MypageViewModel", "회원 탈퇴 중 오류 발생: ${e.message}", e)
            _withdrawState.value = WithdrawState.Error("회원 탈퇴 중 오류가 발생했습니다.")
        }
        Log.d("MypageViewModel", "withdraw() completed")
    }
}