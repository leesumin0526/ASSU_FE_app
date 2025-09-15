package com.example.assu_fe_app.presentation.common.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.data.manager.TokenManager
import com.example.assu_fe_app.domain.model.auth.LoginModel
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.domain.usecase.auth.CommonLoginUseCase
import com.example.assu_fe_app.domain.usecase.auth.LogoutUseCase
import com.example.assu_fe_app.domain.usecase.auth.StudentLoginUseCase
import com.example.assu_fe_app.domain.usecase.deviceToken.UnregisterDeviceTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val studentLoginUseCase: StudentLoginUseCase,
    private val commonLoginUseCase: CommonLoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val unregisterDeviceTokenUseCase: UnregisterDeviceTokenUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {
    
    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState
    
    fun studentLogin(sToken: String, sIdno: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            
            when (val result = studentLoginUseCase(sToken, sIdno)) {
                is RetrofitResult.Success -> {
                    // 토큰 저장
                    tokenManager.saveLoginData(result.data)
                    _loginState.value = LoginState.Success(result.data)
                }
                is RetrofitResult.Fail -> {
                    _loginState.value = LoginState.Error(result.message)
                }
                is RetrofitResult.Error -> {
                    _loginState.value = LoginState.Error("네트워크 연결을 확인해주세요.")
                }
            }
        }
    }
    
    fun commonLogin(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            
            when (val result = commonLoginUseCase(email, password)) {
                is RetrofitResult.Success -> {
                    // 토큰 저장
                    tokenManager.saveLoginData(result.data)
                    _loginState.value = LoginState.Success(result.data)
                }
                is RetrofitResult.Fail -> {
                    _loginState.value = LoginState.Error(result.message)
                }
                is RetrofitResult.Error -> {
                    _loginState.value = LoginState.Error("네트워크 연결을 확인해주세요.")
                }
            }
        }
    }
    
    fun clearLoginState() {
        _loginState.value = LoginState.Idle
    }
    
    fun logout() {
        viewModelScope.launch {
            try {
                // 푸시 토큰 해제 (tokenId가 있는 경우에만)
                val tokenId = tokenManager.getDeviceTokenId()
                if (tokenId != null) {
                    unregisterDeviceTokenUseCase(tokenId)
                }
                // 서버에 로그아웃 API 호출
                logoutUseCase()
            } catch (e: Exception) {
                // API 호출 실패해도 클라이언트 토큰은 삭제
                android.util.Log.e("LoginViewModel", "로그아웃/푸시토큰 해제 API 호출 실패: ${e.message}")
            } finally {
                // 클라이언트 토큰 삭제
                tokenManager.clearTokens()
                _loginState.value = LoginState.Idle
            }
        }
    }
    
    fun checkAutoLogin(): LoginModel? {
        return if (tokenManager.isLoggedIn()) {
            tokenManager.getLoginModel()
        } else {
            null
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val loginData: LoginModel) : LoginState()
    data class Error(val message: String) : LoginState()
    data class PendingApproval(val message: String) : LoginState()
}
