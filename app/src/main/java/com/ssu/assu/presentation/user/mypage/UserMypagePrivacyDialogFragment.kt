package com.ssu.assu.presentation.user.mypage

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import com.ssu.assu.databinding.FragmentUserMypagePrivacyBinding

class UserMypagePrivacyDialogFragment : DialogFragment() {

    private var _binding: FragmentUserMypagePrivacyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserMypagePrivacyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 뒤로가기 버튼 클릭
        binding.btnPrivacyBack.setOnClickListener {
            dismiss()
        }

        // WebView 설정
        setupWebView()
    }

    private fun setupWebView() {
        binding.webviewPrivacyPolicy.apply {
            webViewClient = WebViewClient()
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
            }
            
            // 노션 링크 로드
            loadUrl("https://clumsy-seeder-416.notion.site/ASSU-2591197c19ed800ca456e4980686e6ad?source=copy_link")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
