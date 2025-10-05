package com.assu.app.presentation.common.login

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.assu.app.R
import com.assu.app.databinding.ActivityLoginBinding
import com.assu.app.presentation.admin.AdminMainActivity
import com.assu.app.presentation.base.BaseActivity
import com.assu.app.presentation.common.signup.SignUpActivity
import com.assu.app.presentation.partner.PartnerMainActivity
import com.assu.app.presentation.user.UserMainActivity
import com.assu.app.ui.auth.LoginViewModel
import com.assu.app.ui.auth.LoginViewModel.LoginState
import com.assu.app.ui.deviceToken.DeviceTokenViewModel
import com.assu.app.ui.auth.LoginErrorMessageMapper
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {

    private val deviceTokenViewModel: DeviceTokenViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private var isAutoLoginChecked = false

    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val extraPaddingTop = 3
            v.setPadding(
                systemBars.left,
                systemBars.top + extraPaddingTop.dpToPx(v.context),
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        setLoginButtonEnabled(false)
        binding.etLoginId.addTextChangedListener { checkLoginInputValidity() }
        binding.etLoginPassword.addTextChangedListener { checkLoginInputValidity() }

        binding.btnLogin.setOnClickListener {
            if (!binding.btnLogin.isEnabled) return@setOnClickListener

            val email = binding.etLoginId.text.toString().trim()
            val password = binding.etLoginPassword.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ 화면 종료 전에 토큰 등록까지 먼저 처리
            fetchAndRegisterFcmToken()

            loginViewModel.commonLogin(email, password)
        }

        binding.btnSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.btnLmsLogin.setOnClickListener {
            startActivity(Intent(this, LmsLoginActivity::class.java))
        }
    }

    override fun initObserver() {
        // 로그인 상태 관찰
        loginViewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Idle -> Unit
                is LoginState.Loading -> {
                    setLoginButtonEnabled(false)
                    Log.d("LoginActivity", "로그인 중...")
                }
                is LoginState.Success -> {
                    setLoginButtonEnabled(true)
                    Log.d("LoginActivity", "로그인 성공!")
                    // 자동 로그인 체크 플래그 설정하여 중복 실행 방지
                    isAutoLoginChecked = true
                    // 즉시 메인 화면으로 이동 (FCM 토큰 등록은 메인 화면에서 처리)
                    navigateToMainActivity(state.loginData.userRole)
                }
                is LoginState.Error -> {
                    setLoginButtonEnabled(true)
                    // 로그인 전용 에러 메시지 매퍼 사용
                    val errorMessage = LoginErrorMessageMapper.getLoginErrorMessage(state.fail)
                    Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    Log.e("LoginActivity", "로그인 실패: code=${state.fail.code}, message=${state.fail.message}")
                }
                is LoginState.PendingApproval -> {
                    setLoginButtonEnabled(true)
                    Toast.makeText(this@LoginActivity, "승인 대기 중입니다: ${state.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // FCM 토큰 등록 상태 관찰
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                deviceTokenViewModel.uiState.collect { state ->
                    when (state) {
                        is DeviceTokenViewModel.UiState.Idle -> Unit
                        is DeviceTokenViewModel.UiState.Loading -> {
                            // 필요하면 로딩 표시
                            Log.d("FCM", "디바이스 토큰 등록 중…")
                        }
                        is DeviceTokenViewModel.UiState.Success -> {
                            val tokenId = state.tokenId
                            Log.i("FCM", "등록 성공: ${tokenId}")
                        }
                        is DeviceTokenViewModel.UiState.Fail -> {
                            Log.e("FCM", "등록 실패: ${state.code} ${state.msg}")
                            // FCM 토큰 등록 실패해도 앱을 종료하지 않음
                            // 로그인은 성공했으므로 사용자가 계속 사용할 수 있도록 함
                        }
                        is DeviceTokenViewModel.UiState.Error -> {
                            Log.e("FCM", "등록 오류: ${state.msg}")
                            // FCM 토큰 등록 오류가 발생해도 앱을 종료하지 않음
                        }
                    }
                }
            }
        }
        
        // 자동 로그인 체크 (Observer 설정 후에 호출, 한 번만 실행)
        if (!isAutoLoginChecked) {
            checkAutoLogin()
            isAutoLoginChecked = true
        }
    }


    private fun navigateToMainActivity(userRole: String) {
        val intent = when (userRole.uppercase()) {
            "ADMIN" -> Intent(this, AdminMainActivity::class.java)
            "PARTNER" -> Intent(this, PartnerMainActivity::class.java)
            else -> Intent(this, UserMainActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        
        // 로그인 액티비티 종료하여 매끄러운 전환
        finish()
    }

    private fun checkLoginInputValidity() {
        val id = binding.etLoginId.text?.toString()?.trim()
        val pw = binding.etLoginPassword.text?.toString()?.trim()
        setLoginButtonEnabled(!id.isNullOrEmpty() && !pw.isNullOrEmpty())
    }

    private fun setLoginButtonEnabled(enabled: Boolean) {
        binding.btnLogin.isEnabled = enabled
        binding.btnLogin.background = ContextCompat.getDrawable(
            this,
            if (enabled) R.drawable.btn_basic_selected else R.drawable.btn_basic_unselected
        )
    }


    private fun checkAutoLogin() {
        // 이미 자동 로그인 체크가 완료되었거나 로그인 중이면 건너뛰기
        if (isAutoLoginChecked) {
            Log.d("LoginActivity", "자동 로그인 체크 이미 완료됨 - 건너뛰기")
            return
        }
        
        val loginModel = loginViewModel.checkAutoLogin()
        if (loginModel != null) {
            Log.d("LoginActivity", "자동 로그인 성공 - 메인 화면으로 이동")
            isAutoLoginChecked = true
            navigateToMainActivity(loginModel.userRole)
        } else {
            Log.d("LoginActivity", "자동 로그인 실패 - 로그인 화면 유지")
        }
    }

    private fun Int.dpToPx(context: Context): Int =
        (this * context.resources.displayMetrics.density).toInt()

    //  서버 등록까지 한 번에
    private fun fetchAndRegisterFcmToken() {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "토큰 가져오기 실패", task.exception)
                    // FCM 토큰 가져오기 실패해도 앱을 종료하지 않음
                    return@addOnCompleteListener
                }
                val token = task.result
                if (token.isNullOrEmpty()) {
                    Log.w("FCM", "FCM 토큰이 비어있음")
                    return@addOnCompleteListener
                }
                Log.d("FCM", "FCM 토큰: $token")
                deviceTokenViewModel.register(token)
            }
        } catch (e: Exception) {
            Log.e("FCM", "FCM 토큰 등록 중 예외 발생", e)
            // 예외가 발생해도 앱을 종료하지 않음
        }
    }
}