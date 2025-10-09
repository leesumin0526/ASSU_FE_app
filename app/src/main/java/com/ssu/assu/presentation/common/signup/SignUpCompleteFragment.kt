package com.ssu.assu.presentation.common.signup

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ssu.assu.R
import com.ssu.assu.databinding.FragmentSignUpCompleteBinding
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.presentation.common.login.LoginActivity
import com.ssu.assu.presentation.user.UserMainActivity
import com.ssu.assu.presentation.admin.AdminMainActivity
import com.ssu.assu.presentation.partner.PartnerMainActivity
import com.ssu.assu.ui.auth.SignUpViewModel
import com.ssu.assu.util.showErrorToast
import kotlinx.coroutines.launch

class SignUpCompleteFragment : BaseFragment<FragmentSignUpCompleteBinding>(R.layout.fragment_sign_up_complete){
    
    private val signUpViewModel: SignUpViewModel by activityViewModels()

    override fun initObserver() {
        // 회원가입 결과 관찰 (로딩 완료 후 이름 표시를 위해 로딩 상태 관찰에서 처리)

        // 로딩 상태 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                signUpViewModel.isLoading.collect { isLoading ->
                    Log.d("SignUpCompleteFragment", "로딩 상태 변경: $isLoading")
                    if (!isLoading) {
                        // 로딩이 끝났을 때 회원가입 결과 확인
                        val result = signUpViewModel.signUpResult.value
                        val errorMessage = signUpViewModel.errorMessage.value
                        
                        Log.d("SignUpCompleteFragment", "=== 로딩 완료 후 상태 확인 ===")
                        Log.d("SignUpCompleteFragment", "회원가입 결과: $result")
                        Log.d("SignUpCompleteFragment", "에러 메시지: $errorMessage")
                        
                        if (result == null && errorMessage == null) {
                            // 회원가입 실패 시 LoginActivity로 돌아가기
                            Log.e("SignUpCompleteFragment", "회원가입 실패: 결과도 에러도 없음")
                            Toast.makeText(requireContext(), "회원가입에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show()
                            navigateToLogin()
                        } else if (result != null) {
                            Log.d("SignUpCompleteFragment", "회원가입 성공: $result")
                            // 로딩 완료 후 사용자 이름 표시
                            val userName = result.basicInfo?.name ?: result.username
                            val welcomeText = getString(R.string.signup_welcome_format, userName)
                            binding.tvSignupDoneUsername.text = welcomeText
                            
                            Log.d("SignUpCompleteFragment", "로딩 완료 후 사용자 이름 표시: $userName")
                        }
                        Log.d("SignUpCompleteFragment", "=============================")
                    }
                }
            }
        }

        // 에러 메시지 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                signUpViewModel.errorMessage.collect { error ->
                    error?.let {
                        Log.e("SignUpCompleteFragment", "회원가입 에러 발생: $it")
                        
                        // String을 Fail 객체로 변환하여 표시
                        val fail = com.ssu.assu.util.RetrofitResult.Fail(
                            statusCode = -1,
                            code = "SIGNUP_ERROR",
                            message = it
                        )
                        requireContext().showErrorToast(fail, Toast.LENGTH_LONG)
                        signUpViewModel.clearError()
                        // 에러 발생 시 LoginActivity로 돌아가기
                        navigateToLogin()
                    }
                }
            }
        }
    }

    override fun initView() {
        // 회원가입 API 호출
        signUpViewModel.signUp()

        // 회원가입 완료 후 사용자 타입에 따른 Main Activity로 이동
        binding.btnCompleted.setOnClickListener {
            navigateToMainActivity()
        }
    }

    // 사용자 타입에 따른 Main Activity로 이동하는 함수
    private fun navigateToMainActivity() {
        val userType = signUpViewModel.signUpData.value.userType
        
        val intent = when (userType) {
            "admin" -> Intent(requireContext(), AdminMainActivity::class.java)
            "partner" -> Intent(requireContext(), PartnerMainActivity::class.java)
            "user" -> {
                // 학생 계정의 경우 UserMainActivity로 이동하되 학생 탭으로 이동하도록 설정
                val studentIntent = Intent(requireContext(), UserMainActivity::class.java)
                studentIntent.putExtra("nav_dest_id", R.id.dashboardFragment) // 학생은 대시보드 탭으로 이동
                studentIntent
            }
            else -> Intent(requireContext(), UserMainActivity::class.java)
        }
        
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
    }

    // LoginActivity로 돌아가는 함수
    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
    }
}