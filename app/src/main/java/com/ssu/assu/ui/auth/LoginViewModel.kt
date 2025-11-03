package com.ssu.assu.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssu.assu.data.local.AuthTokenLocalStore
import com.ssu.assu.data.repository.TokenRefreshRepository
import com.ssu.assu.domain.model.auth.LoginModel
import com.ssu.assu.domain.usecase.auth.CommonLoginUseCase
import com.ssu.assu.domain.usecase.auth.StudentLoginUseCase
import com.ssu.assu.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val studentLoginUseCase: StudentLoginUseCase,
    private val commonLoginUseCase: CommonLoginUseCase,
    private val authTokenLocalStore: AuthTokenLocalStore,
    private val tokenRefreshRepository: TokenRefreshRepository
) : ViewModel() {
    
    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState
    
    fun studentLogin(sToken: String, sIdno: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            
            when (val result = studentLoginUseCase(sToken, sIdno)) {
                is RetrofitResult.Success -> {
                    // 토큰 저장
                    authTokenLocalStore.saveLoginData(result.data)
                    
                    // 저장된 정보 로그 출력
                    val savedLoginModel = authTokenLocalStore.getLoginModel()
                    Log.d("LoginViewModel", "=== 학생 로그인 성공 - 저장된 정보 ===")
                    Log.d("LoginViewModel", "Access Token: ${savedLoginModel?.accessToken?.take(20)}...")
                    Log.d("LoginViewModel", "Refresh Token: ${savedLoginModel?.refreshToken?.take(20)}...")
                    Log.d("LoginViewModel", "User ID: ${savedLoginModel?.userId}")
                    Log.d("LoginViewModel", "Username: ${savedLoginModel?.username}")
                    Log.d("LoginViewModel", "User Role: ${savedLoginModel?.userRole}")
                    Log.d("LoginViewModel", "Email(id): ${savedLoginModel?.email}")
                    Log.d("LoginViewModel", "Profile Image Url: ${savedLoginModel?.profileImageUrl}")
                    Log.d("LoginViewModel", "Status: ${savedLoginModel?.status}")
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
                    _loginState.value = LoginState.Error(result)
                }
                is RetrofitResult.Error -> {
                    _loginState.value = LoginState.Error(
                        RetrofitResult.Fail(
                            statusCode = -1,
                            code = "NETWORK_ERROR",
                            message = "네트워크 연결을 확인해주세요."
                        )
                    )
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
                    authTokenLocalStore.saveLoginData(result.data)
                    
                    // 저장된 정보 로그 출력
                    val savedLoginModel = authTokenLocalStore.getLoginModel()
                    Log.d("LoginViewModel", "=== 공통 로그인 성공 - 저장된 정보 ===")
                    Log.d("LoginViewModel", "Access Token: ${savedLoginModel?.accessToken?.take(20)}...")
                    Log.d("LoginViewModel", "Refresh Token: ${savedLoginModel?.refreshToken?.take(20)}...")
                    Log.d("LoginViewModel", "User ID: ${savedLoginModel?.userId}")
                    Log.d("LoginViewModel", "Username: ${savedLoginModel?.username}")
                    Log.d("LoginViewModel", "User Role: ${savedLoginModel?.userRole}")
                    Log.d("LoginViewModel", "Email(id): ${savedLoginModel?.email}")
                    Log.d("LoginViewModel", "Profile Image Url: ${savedLoginModel?.profileImageUrl}")
                    Log.d("LoginViewModel", "Status: ${savedLoginModel?.status}")
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
                    _loginState.value = LoginState.Error(result)
                }
                is RetrofitResult.Error -> {
                    _loginState.value = LoginState.Error(
                        RetrofitResult.Fail(
                            statusCode = -1,
                            code = "NETWORK_ERROR",
                            message = "네트워크 연결을 확인해주세요."
                        )
                    )
                }
            }
        }
    }

    fun checkAutoLogin(): LoginModel? {
        return if (authTokenLocalStore.isLoggedIn()) {
            authTokenLocalStore.getLoginModel()
        } else {
            null
        }
    }
    
    /**
     * 앱 시작 시 저장된 토큰으로 자동 로그인 시도
     * 토큰이 만료되었으면 리프레시를 시도하고, 성공하면 LoginModel 반환
     */
    fun checkAutoLoginWithRefresh() {
        viewModelScope.launch {
            Log.d("LoginViewModel", "=== 자동 로그인 체크 시작 ===")
            
            if (!authTokenLocalStore.isLoggedIn()) {
                Log.d("LoginViewModel", "저장된 로그인 정보 없음")
                return@launch
            }
            
            val accessToken = authTokenLocalStore.getAccessToken()
            val refreshToken = authTokenLocalStore.getRefreshToken()
            
            if (accessToken == null || refreshToken == null) {
                Log.w("LoginViewModel", "토큰 정보 불완전 - 자동 로그인 불가")
                authTokenLocalStore.clearTokens()
                return@launch
            }
            
            Log.d("LoginViewModel", "저장된 토큰 확인 완료")
            
            // 토큰이 만료되었거나 곧 만료될 예정이면 리프레시 시도
            val isExpiringSoon = authTokenLocalStore.isAccessTokenExpiringSoon()
            val isExpired = authTokenLocalStore.isAccessTokenExpired()
            
            Log.d("LoginViewModel", "토큰 만료: $isExpired, 곧 만료: $isExpiringSoon")
            
            if (isExpired || isExpiringSoon) {
                Log.i("LoginViewModel", "토큰 리프레시 시도...")
                _loginState.value = LoginState.Loading
                
                when (val result = tokenRefreshRepository.refreshToken()) {
                    is RetrofitResult.Success -> {
                        Log.i("LoginViewModel", "✅ 토큰 리프레시 성공 - 자동 로그인")
                        val loginModel = authTokenLocalStore.getLoginModel()
                        if (loginModel != null) {
                            _loginState.value = LoginState.Success(loginModel)
                        } else {
                            Log.e("LoginViewModel", "리프레시 후 LoginModel이 null")
                        }
                    }
                    is RetrofitResult.Fail -> {
                        Log.e("LoginViewModel", "❌ 토큰 리프레시 실패: ${result.message}")
                        authTokenLocalStore.clearTokens()
                        _loginState.value = LoginState.Idle
                    }
                    is RetrofitResult.Error -> {
                        Log.e("LoginViewModel", "❌ 토큰 리프레시 오류: ${result.exception.message}")
                        // 네트워크 오류는 토큰을 삭제하지 않음
                        _loginState.value = LoginState.Idle
                    }
                }
            } else {
                // 토큰이 유효하면 바로 자동 로그인
                Log.i("LoginViewModel", "✅ 토큰 유효 - 자동 로그인")
                val loginModel = authTokenLocalStore.getLoginModel()
                if (loginModel != null) {
                    _loginState.value = LoginState.Success(loginModel)
                } else {
                    Log.e("LoginViewModel", "LoginModel이 null")
                }
            }
        }
    }

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val loginData: LoginModel) : LoginState()
        data class Error(val fail: RetrofitResult.Fail) : LoginState()
        data class PendingApproval(val message: String) : LoginState()
    }
}
