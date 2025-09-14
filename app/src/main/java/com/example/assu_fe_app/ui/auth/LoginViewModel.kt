package com.example.assu_fe_app.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.data.manager.TokenManager
import com.example.assu_fe_app.domain.model.auth.LoginModel
import com.example.assu_fe_app.domain.usecase.auth.CommonLoginUseCase
import com.example.assu_fe_app.domain.usecase.auth.LogoutUseCase
import com.example.assu_fe_app.domain.usecase.auth.StudentLoginUseCase
import com.example.assu_fe_app.domain.usecase.auth.WithdrawUseCase
import com.example.assu_fe_app.domain.usecase.deviceToken.UnregisterDeviceTokenUseCase
import com.example.assu_fe_app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val studentLoginUseCase: StudentLoginUseCase,
    private val commonLoginUseCase: CommonLoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val withdrawUseCase: WithdrawUseCase,
    private val unregisterDeviceTokenUseCase: UnregisterDeviceTokenUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {
    
    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState
    
    private val _withdrawState = MutableLiveData<WithdrawState>()
    val withdrawState: LiveData<WithdrawState> = _withdrawState
    
    fun studentLogin(sToken: String, sIdno: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            
            when (val result = studentLoginUseCase(sToken, sIdno)) {
                is RetrofitResult.Success -> {
                    // 토큰 저장
                    tokenManager.saveLoginData(result.data)
                    
                    // 저장된 정보 로그 출력
                    val savedLoginModel = tokenManager.getLoginModel()
                    Log.d("LoginViewModel", "=== 학생 로그인 성공 - 저장된 정보 ===")
                    Log.d("LoginViewModel", "User ID: ${savedLoginModel?.userId}")
                    Log.d("LoginViewModel", "Username: ${savedLoginModel?.username}")
                    Log.d("LoginViewModel", "User Role: ${savedLoginModel?.userRole}")
                    Log.d("LoginViewModel", "Status: ${savedLoginModel?.status}")
                    Log.d("LoginViewModel", "Access Token: ${savedLoginModel?.accessToken?.take(20)}...")
                    Log.d("LoginViewModel", "Refresh Token: ${savedLoginModel?.refreshToken?.take(20)}...")
                    savedLoginModel?.basicInfo?.let { basicInfo ->
                        Log.d("LoginViewModel", "=== Basic Info ===")
                        Log.d("LoginViewModel", "Name: ${basicInfo.name}")
                        Log.d("LoginViewModel", "University: ${basicInfo.university}")
                        Log.d("LoginViewModel", "Department: ${basicInfo.department}")
                        Log.d("LoginViewModel", "Major: ${basicInfo.major}")
                    } ?: Log.d("LoginViewModel", "Basic Info: null")
                    Log.d("LoginViewModel", "================================")
                    
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
                    
                    // 저장된 정보 로그 출력
                    val savedLoginModel = tokenManager.getLoginModel()
                    Log.d("LoginViewModel", "=== 공통 로그인 성공 - 저장된 정보 ===")
                    Log.d("LoginViewModel", "User ID: ${savedLoginModel?.userId}")
                    Log.d("LoginViewModel", "Username: ${savedLoginModel?.username}")
                    Log.d("LoginViewModel", "User Role: ${savedLoginModel?.userRole}")
                    Log.d("LoginViewModel", "Status: ${savedLoginModel?.status}")
                    Log.d("LoginViewModel", "Access Token: ${savedLoginModel?.accessToken?.take(20)}...")
                    Log.d("LoginViewModel", "Refresh Token: ${savedLoginModel?.refreshToken?.take(20)}...")
                    savedLoginModel?.basicInfo?.let { basicInfo ->
                        Log.d("LoginViewModel", "=== Basic Info ===")
                        Log.d("LoginViewModel", "Name: ${basicInfo.name}")
                        Log.d("LoginViewModel", "University: ${basicInfo.university}")
                        Log.d("LoginViewModel", "Department: ${basicInfo.department}")
                        Log.d("LoginViewModel", "Major: ${basicInfo.major}")
                    } ?: Log.d("LoginViewModel", "Basic Info: null")
                    Log.d("LoginViewModel", "================================")
                    
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
                Log.e("LoginViewModel", "로그아웃/푸시토큰 해제 API 호출 실패: ${e.message}")
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
    
    fun withdraw() {
        viewModelScope.launch {
            _withdrawState.value = WithdrawState.Loading
            
            try {
                // 푸시 토큰 해제 (tokenId가 있는 경우에만)
                val tokenId = tokenManager.getDeviceTokenId()
                if (tokenId != null) {
                    unregisterDeviceTokenUseCase(tokenId)
                }
                
                // 서버에 회원 탈퇴 API 호출
                when (val result = withdrawUseCase()) {
                    is RetrofitResult.Success -> {
                        // 클라이언트 토큰 삭제
                        tokenManager.clearTokens()
                        _withdrawState.value = WithdrawState.Success
                    }
                    is RetrofitResult.Fail -> {
                        _withdrawState.value = WithdrawState.Error(result.message)
                    }
                    is RetrofitResult.Error -> {
                        _withdrawState.value = WithdrawState.Error("네트워크 연결을 확인해주세요.")
                    }
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "회원 탈퇴 API 호출 실패: ${e.message}")
                _withdrawState.value = WithdrawState.Error("회원 탈퇴 중 오류가 발생했습니다.")
            }
        }
    }
    
    fun clearWithdrawState() {
        _withdrawState.value = WithdrawState.Idle
    }

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val loginData: LoginModel) : LoginState()
        data class Error(val message: String) : LoginState()
        data class PendingApproval(val message: String) : LoginState()
    }

    sealed class WithdrawState {
        object Idle : WithdrawState()
        object Loading : WithdrawState()
        object Success : WithdrawState()
        data class Error(val message: String) : WithdrawState()
    }
}
