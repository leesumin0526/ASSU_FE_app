package com.example.assu_fe_app.presentation.user.signup

import android.annotation.SuppressLint
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentUserSignUpStudentBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.util.setProgressBarFillAnimated

class UserSignUpStudentFragment :
    BaseFragment<FragmentUserSignUpStudentBinding>(R.layout.fragment_user_sign_up_student) {

    override fun initObserver() {}

    override fun initView() {
        binding.ivSignupProgressBar.setProgressBarFillAnimated(
            container = binding.flSignupProgressContainer,
            fromPercent = 0.55f,
            toPercent = 0.70f
        )

        val schoolName = "숭실대학교" // 이후 동적으로 변경 가능
        val baseText = getString(R.string.school_account_text, schoolName)

        // 텍스트에 "숭실대학교" 부분을 assu_main 컬러로 설정
        binding.tvSchoolAccountTitle.text = buildSpannedString {
            append(" ")
            color(ContextCompat.getColor(requireContext(), R.color.assu_main)) {
                append(schoolName)
            }
            append(" 학생이시군요!\n재학중이신 학교를\n인증해주세요!")
        }

        // WebView 설정
        setupWebView()

        // LMS 인증하기 버튼 클릭
        binding.btnLmsAuth.setOnClickListener {
            showLmsAuthWebView()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        binding.webviewLmsAuth.settings.apply {
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

        binding.webviewLmsAuth.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let { currentUrl ->
                    // 유세인트 SSO 로그인 완료 URL 패턴 감지
                    if (currentUrl.startsWith("https://saint.ssu.ac.kr/webSSO/sso.jsp")) {
                        try {
                            val queryString = currentUrl.split("?")[1]
                            val sToken = queryString.split("sToken=")[1].split("&")[0]
                            val sIdno = queryString.split("sIdno=")[1]
                            handleLmsAuthSuccess(sToken, sIdno)
                            return true
                        } catch (e: Exception) {
                            handleLmsAuthSuccess()
                            return true
                        }
                    }
                    
                    // 일반적인 유세인트 메인 페이지로의 리다이렉트 감지
                    if (currentUrl.contains("saint.ssu.ac.kr") && 
                        (currentUrl.contains("main") || currentUrl.contains("index") || currentUrl.contains("dashboard") || currentUrl.contains("portal"))) {
                        handleLmsAuthSuccess()
                        return true
                    }
                    
                    view?.loadUrl(currentUrl)
                }
                return true
            }
            
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                url?.let { currentUrl ->
                    if (currentUrl.startsWith("https://saint.ssu.ac.kr/webSSO/sso.jsp")) {
                        try {
                            val queryString = currentUrl.split("?")[1]
                            val sToken = queryString.split("sToken=")[1].split("&")[0]
                            val sIdno = queryString.split("sIdno=")[1]
                            handleLmsAuthSuccess(sToken, sIdno)
                        } catch (e: Exception) {
                            handleLmsAuthSuccess()
                        }
                    } else if (currentUrl.contains("saint.ssu.ac.kr") && 
                              (currentUrl.contains("main") || currentUrl.contains("index") || currentUrl.contains("dashboard") || currentUrl.contains("portal"))) {
                        handleLmsAuthSuccess()
                    }
                }
            }
        }
    }

    private fun showLmsAuthWebView() {
        // 기본 컨텐츠 숨기고 WebView 표시
        binding.llDefaultContent.visibility = View.GONE
        binding.webviewLmsAuth.visibility = View.VISIBLE
        
        // LMS 로그인 페이지 로드
        binding.webviewLmsAuth.loadUrl("https://smartid.ssu.ac.kr/Symtra_sso/smln.asp?apiReturnUrl=https%3A%2F%2Fsaint.ssu.ac.kr%2FwebSSO%2Fsso.jsp")
    }

    private fun handleLmsAuthSuccess(sToken: String? = null, sIdno: String? = null) {
        // LMS 인증 성공 시 처리
        android.widget.Toast.makeText(requireContext(), "LMS 인증 성공!", android.widget.Toast.LENGTH_SHORT).show()
        
        // 토큰이 있다면 로그 (디버깅용)
        if (sToken != null && sIdno != null) {
            android.util.Log.d("UserSignUpStudentFragment", "Token: $sToken, ID: $sIdno")
        }
        
        // WebView 숨기고 기본 컨텐츠 복원
        binding.webviewLmsAuth.visibility = View.GONE
        binding.llDefaultContent.visibility = View.VISIBLE
        
        // LMS 인증 성공 후 학생 정보 확인 화면으로 이동
        findNavController().navigate(R.id.action_user_student_to_student_check)
    }

    // 유세인트 인증 로직 추가 예정
    private fun validateStudentAccount(id: String, pw: String): Boolean {
        // 임시 로직: "20211234" "1234"일 경우만 통과
        return id == "20211234" && pw == "1234"
    }
}
