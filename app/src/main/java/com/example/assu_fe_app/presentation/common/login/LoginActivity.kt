package com.example.assu_fe_app.presentation.common.login

import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.ActivityLoginBinding
import com.example.assu_fe_app.presentation.admin.AdminMainActivity
import com.example.assu_fe_app.presentation.base.BaseActivity
import com.example.assu_fe_app.presentation.common.signup.SignUpActivity
import com.example.assu_fe_app.presentation.partner.PartnerMainActivity

class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {
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

        // 초기 버튼 비활성화
        setLoginButtonEnabled(false)

        // 텍스트 변경 감지 리스너 등록
        binding.etLoginId.addTextChangedListener { checkLoginInputValidity() }
        binding.etLoginPassword.addTextChangedListener { checkLoginInputValidity() }

        // 로그인 클릭 시
        binding.btnLogin.setOnClickListener {
            if (!binding.btnLogin.isEnabled) return@setOnClickListener

            val id = binding.etLoginId.text.toString()
            val pw = binding.etLoginPassword.text.toString()

            when (getUserRole(id, pw)) {
                CommonUserRole.ADMIN -> startActivity(Intent(this, AdminMainActivity::class.java))
                CommonUserRole.PARTNER -> startActivity(Intent(this, PartnerMainActivity::class.java))
                CommonUserRole.INVALID -> Toast.makeText(this, "아이디 또는 비밀번호가 올바르지 않습니다", Toast.LENGTH_SHORT).show()
            }

            finish()
        }

        // LMS 학생 로그인 버튼 클릭 시
        binding.btnLmsLogin.setOnClickListener {
            val intent = Intent(this, LmsLoginActivity::class.java)
            startActivity(intent)
        }

        // 회원가입하기 텍스트에 밑줄 추가
        val signupText = SpannableString("회원가입하기")
        signupText.setSpan(UnderlineSpan(), 0, signupText.length, 0)
        binding.btnSignup.text = signupText

        // 회원가입하기 클릭 시 회원가입 이동
        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    // 블랙박스 역할: 로그인 정보로 역할 판단
    private fun getUserRole(id: String, pw: String): CommonUserRole {
        return when {
            id == "admin" && pw == "1234" -> CommonUserRole.ADMIN
            id == "partner" && pw == "1234" -> CommonUserRole.PARTNER
            else -> CommonUserRole.INVALID
        }
    }

    // 입력값 유효성 확인 함수
    private fun checkLoginInputValidity() {
        val id = binding.etLoginId.text?.toString()?.trim()
        val pw = binding.etLoginPassword.text?.toString()?.trim()
        val isValid = !id.isNullOrEmpty() && !pw.isNullOrEmpty()
        setLoginButtonEnabled(isValid)
    }

    // 버튼 배경 및 활성 상태 설정 함수
    private fun setLoginButtonEnabled(enabled: Boolean) {
        binding.btnLogin.isEnabled = enabled
        binding.btnLogin.background = ContextCompat.getDrawable(
            this,
            if (enabled) R.drawable.btn_basic_selected else R.drawable.btn_basic_unselected
        )
        binding.btnLogin.backgroundTintList = ContextCompat.getColorStateList(
            this,
            if (enabled) R.color.assu_main else R.color.gray_light
        )
    }

    // 사용자 역할 정의
    enum class CommonUserRole {
        ADMIN, PARTNER, INVALID
    }

    override fun initObserver() {
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}