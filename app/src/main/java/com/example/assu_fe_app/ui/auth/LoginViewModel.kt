package com.example.assu_fe_app.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import com.example.assu_fe_app.domain.model.auth.LoginModel
import com.example.assu_fe_app.domain.usecase.auth.CommonLoginUseCase
import com.example.assu_fe_app.domain.usecase.auth.StudentLoginUseCase
import com.example.assu_fe_app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val studentLoginUseCase: StudentLoginUseCase,
    private val commonLoginUseCase: CommonLoginUseCase,
    private val authTokenLocalStore: AuthTokenLocalStore
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

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val loginData: LoginModel) : LoginState()
        data class Error(val fail: RetrofitResult.Fail) : LoginState()
        data class PendingApproval(val message: String) : LoginState()
    }
}
