package com.example.assu_fe_app.presentation.common.signup

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentSignUpCompleteBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.user.UserMainActivity
import com.example.assu_fe_app.ui.auth.SignUpViewModel
import kotlinx.coroutines.launch

class SignUpCompleteFragment : BaseFragment<FragmentSignUpCompleteBinding>(R.layout.fragment_sign_up_complete){
    
    private val signUpViewModel: SignUpViewModel by activityViewModels()

    override fun initObserver() {
        // 회원가입 결과 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                signUpViewModel.signUpResult.collect { result ->
                    result?.let {
                        // 회원가입 성공 시 사용자 이름 표시
                        val welcomeText = getString(R.string.signup_welcome_format, it.username)
                        binding.tvSignupDoneUsername.text = welcomeText
                    }
                }
            }
        }

        // 에러 메시지 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                signUpViewModel.errorMessage.collect { error ->
                    error?.let {
                        android.widget.Toast.makeText(requireContext(), it, android.widget.Toast.LENGTH_SHORT).show()
                        signUpViewModel.clearError()
                    }
                }
            }
        }
    }

    override fun initView() {
        // 회원가입 API 호출
        signUpViewModel.signUp()

        // 회원가입 완료 후 Main Activity로 이동
        binding.btnCompleted.setOnClickListener {
            val intent = Intent(requireContext(), UserMainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}