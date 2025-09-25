package com.example.assu_fe_app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.data.dto.auth.PhoneVerificationSendRequestDto
import com.example.assu_fe_app.data.dto.auth.PhoneVerificationVerifyRequestDto
import com.example.assu_fe_app.data.repository.auth.AuthRepository
import com.example.assu_fe_app.util.onError
import com.example.assu_fe_app.util.onFail
import com.example.assu_fe_app.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpVerifyViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // 인증번호 발송
    sealed interface SendPhoneVerificationUiState {
        data object Idle : SendPhoneVerificationUiState
        data object Loading : SendPhoneVerificationUiState
        data object Success : SendPhoneVerificationUiState
        data class Fail(val code: Int, val message: String?) : SendPhoneVerificationUiState
        data class Error(val message: String) : SendPhoneVerificationUiState
    }

    private val _sendPhoneVerificationState = MutableLiveData<SendPhoneVerificationUiState>()
    val sendPhoneVerificationState: LiveData<SendPhoneVerificationUiState> = _sendPhoneVerificationState

    fun sendPhoneVerification(phoneNumber: String) {
        // 이미 로딩 중이면 중복 요청 방지
        if (_sendPhoneVerificationState.value is SendPhoneVerificationUiState.Loading) {
            return
        }
        
        viewModelScope.launch {
            _sendPhoneVerificationState.value = SendPhoneVerificationUiState.Loading
            
            val request = PhoneVerificationSendRequestDto(phoneNumber = phoneNumber)
            authRepository.sendPhoneVerification(request)
                .onSuccess { _sendPhoneVerificationState.value = SendPhoneVerificationUiState.Success }
                .onFail { code -> _sendPhoneVerificationState.value = SendPhoneVerificationUiState.Fail(code, "인증번호 발송 실패") }
                .onError { e -> _sendPhoneVerificationState.value = SendPhoneVerificationUiState.Error(e.message ?: "Unknown Error") }
        }
    }

    // 인증번호 검증
    sealed interface VerifyPhoneVerificationUiState {
        data object Idle : VerifyPhoneVerificationUiState
        data object Loading : VerifyPhoneVerificationUiState
        data object Success : VerifyPhoneVerificationUiState
        data class Fail(val code: Int, val message: String?) : VerifyPhoneVerificationUiState
        data class Error(val message: String) : VerifyPhoneVerificationUiState
    }

    private val _verifyPhoneVerificationState = MutableLiveData<VerifyPhoneVerificationUiState>()
    val verifyPhoneVerificationState: LiveData<VerifyPhoneVerificationUiState> = _verifyPhoneVerificationState

    fun verifyPhoneVerification(phoneNumber: String, authNumber: String) {
        // 이미 로딩 중이면 중복 요청 방지
        if (_verifyPhoneVerificationState.value is VerifyPhoneVerificationUiState.Loading) {
            return
        }
        
        viewModelScope.launch {
            _verifyPhoneVerificationState.value = VerifyPhoneVerificationUiState.Loading
            
            val request = PhoneVerificationVerifyRequestDto(
                phoneNumber = phoneNumber,
                authNumber = authNumber
            )
            authRepository.verifyPhoneVerification(request)
                .onSuccess { _verifyPhoneVerificationState.value = VerifyPhoneVerificationUiState.Success }
                .onFail { code -> _verifyPhoneVerificationState.value = VerifyPhoneVerificationUiState.Fail(code, "인증번호 검증 실패") }
                .onError { e -> _verifyPhoneVerificationState.value = VerifyPhoneVerificationUiState.Error(e.message ?: "Unknown Error") }
        }
    }
}