package com.assu.app.presentation.common.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.assu.app.R
import com.assu.app.databinding.ActivityLmsLoginBinding
import com.assu.app.presentation.base.BaseActivity
import com.assu.app.presentation.user.UserMainActivity
import com.assu.app.ui.auth.LoginViewModel
import com.assu.app.ui.auth.LoginViewModel.LoginState
import com.assu.app.ui.deviceToken.DeviceTokenViewModel
import com.assu.app.ui.auth.LoginErrorMessageMapper
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LmsLoginActivity : BaseActivity<ActivityLmsLoginBinding>(R.layout.activity_lms_login) {
    
    private lateinit var webView: WebView
    private lateinit var btnBack: ImageButton
    private val loginViewModel: LoginViewModel by viewModels()
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
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        webView = binding.webviewLms
        btnBack = binding.btnBack
        
        // 뒤로가기 버튼 클릭
        btnBack.setOnClickListener {
            finish()
        }
        
        // WebView 설정
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            allowFileAccess = true
            allowContentAccess = true
        }
        
        // JavaScript 인터페이스 추가
        webView.addJavascriptInterface(WebAppInterface(), "Android")
        
        // WebViewClient 설정 (외부 브라우저로 이동하지 않도록)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let { currentUrl ->
                    // 유세인트 SSO 로그인 완료 URL 패턴 감지
                    if (currentUrl.startsWith("https://saint.ssu.ac.kr/webSSO/sso.jsp")) {
                        // URL에서 토큰 추출 (React Native 로직 참고)
                        try {
                            val queryString = currentUrl.split("?")[1]
                            val sToken = queryString.split("sToken=")[1].split("&")[0]
                            val sIdno = queryString.split("sIdno=")[1]
                            
                            // 토큰 추출 성공 시 로그인 성공으로 처리
                            handleLoginSuccess(sToken, sIdno)
                            return true
                        } catch (e: Exception) {
                            // 토큰 추출 실패 시 일반적인 로그인 성공 처리
                            handleLoginSuccess()
                            return true
                        }
                    }
                    
                    // 일반적인 유세인트 메인 페이지로의 리다이렉트 감지
                    if (currentUrl.contains("saint.ssu.ac.kr") && 
                        (currentUrl.contains("main") || currentUrl.contains("index") || currentUrl.contains("dashboard") || currentUrl.contains("portal"))) {
                        handleLoginSuccess()
                        return true
                    }
                    
                    view?.loadUrl(currentUrl)
                }
                return true
            }
            
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // 페이지 로딩 완료 후 URL 확인
                url?.let { currentUrl ->
                    if (currentUrl.startsWith("https://saint.ssu.ac.kr/webSSO/sso.jsp")) {
                        try {
                            val queryString = currentUrl.split("?")[1]
                            val sToken = queryString.split("sToken=")[1].split("&")[0]
                            val sIdno = queryString.split("sIdno=")[1]
                            handleLoginSuccess(sToken, sIdno)
                        } catch (e: Exception) {
                            handleLoginSuccess()
                        }
                    } else if (currentUrl.contains("saint.ssu.ac.kr") && 
                              (currentUrl.contains("main") || currentUrl.contains("index") || currentUrl.contains("dashboard") || currentUrl.contains("portal"))) {
                        handleLoginSuccess()
                    }
                }
            }
        }
        
        // LMS 로그인 페이지 로드
        webView.loadUrl("https://smartid.ssu.ac.kr/Symtra_sso/smln.asp?apiReturnUrl=https%3A%2F%2Fsaint.ssu.ac.kr%2FwebSSO%2Fsso.jsp")
    }
    
    private fun handleLoginSuccess(sToken: String? = null, sIdno: String? = null) {
        // 토큰이 있다면 서버 로그인 시도
        if (sToken != null && sIdno != null) {
            Log.d("LmsLoginActivity", "SToken: $sToken, SID: $sIdno")
            loginViewModel.studentLogin(sToken, sIdno)
        } else {
            // 토큰이 없는 경우 기본적으로 UserMainActivity로 이동
            Log.w("LmsLoginActivity", "토큰 정보가 없어 기본 화면으로 이동합니다.")
            navigateToMainActivity("USER")
        }
    }
    
    private fun navigateToMainActivity(userRole: String) {
        val intent = when (userRole.uppercase()) {
             "USER" -> Intent(this, UserMainActivity::class.java)
             else -> Intent(this, UserMainActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
    
    // JavaScript 인터페이스 클래스
    inner class WebAppInterface {
        @JavascriptInterface
        fun onLoginSuccess() {
            // JavaScript에서 호출되는 로그인 성공 콜백
            runOnUiThread {
                handleLoginSuccess()
            }
        }
        
        @JavascriptInterface
        fun onLoginError(message: String) {
            // JavaScript에서 호출되는 로그인 실패 콜백
            runOnUiThread {
                android.widget.Toast.makeText(this@LmsLoginActivity, "로그인 실패: $message", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun initObserver() {
        loginViewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Idle -> {
                    hideLoading()
                }
                is LoginState.Loading -> {
                    showLoading("로그인 중...")
                    Log.d("LmsLoginActivity", "서버 로그인 중...")
                }
                is LoginState.Success -> {
                    hideLoading()
                    Log.d("LmsLoginActivity", "서버 로그인 성공!")
                    // FCM 토큰 등록
                    fetchAndRegisterFcmToken()
                    navigateToMainActivity(state.loginData.userRole)
                }
                is LoginState.Error -> {
                    hideLoading()
                    // 로그인 전용 에러 메시지 매퍼 사용
                    val errorMessage = LoginErrorMessageMapper.getLoginErrorMessage(state.fail)
                    Toast.makeText(this@LmsLoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("LmsLoginActivity", "서버 로그인 실패: code=${state.fail.code}, message=${state.fail.message}")
                }
                is LoginState.PendingApproval -> {
                    hideLoading()
                    Toast.makeText(this@LmsLoginActivity, "승인 대기 중입니다: ${state.message}", Toast.LENGTH_SHORT).show()
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
    }

    // FCM 토큰 등록 함수
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

    private fun showLoading(message: String = "로딩 중...") {
        binding.loadingOverlay.visibility = android.view.View.VISIBLE
        binding.tvLoadingText.text = message
    }
    
    private fun hideLoading() {
        binding.loadingOverlay.visibility = android.view.View.GONE
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}
