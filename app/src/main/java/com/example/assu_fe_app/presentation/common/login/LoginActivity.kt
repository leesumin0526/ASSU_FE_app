package com.example.assu_fe_app.presentation.common.login

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
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.ActivityLoginBinding
import com.example.assu_fe_app.presentation.admin.AdminMainActivity
import com.example.assu_fe_app.presentation.base.BaseActivity
import com.example.assu_fe_app.presentation.common.signup.SignUpActivity
import com.example.assu_fe_app.presentation.partner.PartnerMainActivity
import com.example.assu_fe_app.presentation.user.UserMainActivity
import com.example.assu_fe_app.ui.deviceToken.DeviceTokenViewModel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {

    private val deviceTokenViewModel: DeviceTokenViewModel by viewModels()

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

            val id = binding.etLoginId.text.toString()
            val pw = binding.etLoginPassword.text.toString()

            when (getUserRole(id, pw)) {
                UserRole.ADMIN   -> startActivity(Intent(this, AdminMainActivity::class.java))
                UserRole.PARTNER -> startActivity(Intent(this, PartnerMainActivity::class.java))
                UserRole.USER    -> startActivity(Intent(this, UserMainActivity::class.java))
                UserRole.INVALID -> {
                    Toast.makeText(this, "아이디 또는 비밀번호가 올바르지 않습니다", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // ✅ 화면 종료 전에 토큰 등록까지 먼저 처리
            fetchAndRegisterFcmToken()
        }

        binding.btnSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    override fun initObserver() {
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
                            finish() // 실패해도 로그인은 진행했으니 종료할지, 남을지는 정책대로
                        }
                        is DeviceTokenViewModel.UiState.Error -> {
                            Log.e("FCM", "등록 오류: ${state.msg}")
                            finish()
                        }
                    }
                }
            }
        }
    }


    private fun getUserRole(id: String, pw: String): UserRole = when {
        id == "admin" && pw == "1234" -> UserRole.ADMIN
        id == "partner" && pw == "1234" -> UserRole.PARTNER
        id == "1" && pw == "1" -> UserRole.USER
        else -> UserRole.INVALID
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

    enum class UserRole { ADMIN, PARTNER, USER, INVALID }

    private fun Int.dpToPx(context: Context): Int =
        (this * context.resources.displayMetrics.density).toInt()

    //  서버 등록까지 한 번에
    private fun fetchAndRegisterFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "토큰 가져오기 실패", task.exception)
                deviceTokenViewModel.register("") // 빈값 보내지 말고 여기서 종료하는 게 나음
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM", "FCM 토큰: $token")
            deviceTokenViewModel.register(token)
        }
    }
}