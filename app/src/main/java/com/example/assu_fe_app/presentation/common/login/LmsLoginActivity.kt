package com.example.assu_fe_app.presentation.common.login

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.ActivityLmsLoginBinding
import com.example.assu_fe_app.presentation.base.BaseActivity

class LmsLoginActivity : BaseActivity<ActivityLmsLoginBinding>(R.layout.activity_lms_login) {
    
    private lateinit var webView: WebView
    private lateinit var btnBack: ImageButton

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
        
        // LMS 로그인 페이지 로드 (React Native와 동일한 URL 사용)
        webView.loadUrl("https://smartid.ssu.ac.kr/Symtra_sso/smln.asp?apiReturnUrl=https%3A%2F%2Fsaint.ssu.ac.kr%2FwebSSO%2Fsso.jsp")
    }
    
    private fun handleLoginSuccess(sToken: String? = null, sIdno: String? = null) {
        // 로그인 성공 시 처리
        android.widget.Toast.makeText(this, "유세인트 로그인 성공!", android.widget.Toast.LENGTH_SHORT).show()
        
        // 토큰이 있다면 로그 (디버깅용)
        if (sToken != null && sIdno != null) {
            android.util.Log.d("LmsLoginActivity", "Token: $sToken, ID: $sIdno")
        }
        
        // 1초 후 UserMainActivity로 자동 이동
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val intent = android.content.Intent(this, com.example.assu_fe_app.presentation.user.UserMainActivity::class.java)
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }, 1000)
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
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}
