package com.example.assu_fe_app.presentation.common.signup

import android.content.Context
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentSignUpVerifyBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.ui.auth.SignUpViewModel
import com.example.assu_fe_app.ui.auth.SignUpVerifyViewModel
import com.example.assu_fe_app.ui.auth.SignUpVerifyViewModel.SendPhoneVerificationUiState
import com.example.assu_fe_app.ui.auth.SignUpVerifyViewModel.VerifyPhoneVerificationUiState
import com.example.assu_fe_app.util.setProgressBarFillAnimated
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpVerifyFragment :
    BaseFragment<FragmentSignUpVerifyBinding>(R.layout.fragment_sign_up_verify) {

    private val viewModel: SignUpVerifyViewModel by viewModels()
    private val signUpViewModel: SignUpViewModel by activityViewModels()

    private var countDownTimer: CountDownTimer? = null
    private val totalTimeMillis = 5 * 60 * 1000L // 5분
    private var isVerified = false

    private var lastTimerText: String = "05:00"

    private var timerStartTime: Long = 0L
    private var timerEndTime: Long = 0L

    override fun initObserver() {
        viewModel.sendPhoneVerificationState.observe(this) { state ->
            when (state) {
                is SendPhoneVerificationUiState.Success -> {
                    startVerificationUI()
                    startTimer()
                }
                is SendPhoneVerificationUiState.Fail -> {
                    Log.d("SignUpVerifyFragment", "전화번호 인증 실패: code=${state.code}, message=${state.message}")
                    // 버튼 재활성화
                    binding.tvUserVerifyPhone.isEnabled = true
                    // 서버 에러 메시지는 토스트로 표시하지 않음
                    // 사용자에게는 간단한 안내 메시지만 표시
                    val errorMessage = when {
                        state.code == 400 -> "전화번호 형식이 올바르지 않습니다."
                        state.code == 429 -> "요청이 너무 많습니다. 잠시 후 다시 시도해주세요."
                        else -> "인증번호 발송에 실패했습니다. 다시 시도해주세요."
                    }
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
                is SendPhoneVerificationUiState.Error -> {
                    Log.d("SignUpVerifyViewModel", "네트워크 에러: ${state.message}")
                    // 버튼 재활성화
                    binding.tvUserVerifyPhone.isEnabled = true
                    Toast.makeText(requireContext(), "네트워크 연결을 확인해주세요.", Toast.LENGTH_SHORT).show()
                }
                is SendPhoneVerificationUiState.Idle -> {
                    // 버튼 활성화
                    binding.tvUserVerifyPhone.isEnabled = true
                }
                is SendPhoneVerificationUiState.Loading -> {
                    // 버튼 비활성화 (중복 클릭 방지)
                    binding.tvUserVerifyPhone.isEnabled = false
                }
            }
        }

        viewModel.verifyPhoneVerificationState.observe(this) { state ->
            when (state) {
                is VerifyPhoneVerificationUiState.Success -> {
                    successVerificationUI()
                }
                is VerifyPhoneVerificationUiState.Fail -> {
                    Log.d("SignUpVerifyFragment", "인증번호 검증 실패: code=${state.code}, message=${state.message}")
                    errorVerificationUI()
                    // 버튼 재활성화
                    binding.btnCompleted.isEnabled = true
                    // 서버 에러 메시지는 토스트로 표시하지 않음
                    // 사용자에게는 간단한 안내 메시지만 표시
                    val errorMessage = when {
                        state.code == 400 -> "인증번호가 올바르지 않습니다."
                        state.code == 429 -> "요청이 너무 많습니다. 잠시 후 다시 시도해주세요."
                        else -> "인증번호가 올바르지 않습니다."
                    }
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
                is VerifyPhoneVerificationUiState.Error -> {
                    errorVerificationUI()
                    Log.d("SignUpVerifyViewModel", "네트워크 에러: ${state.message}")
                    // 버튼 재활성화
                    binding.btnCompleted.isEnabled = true
                    Toast.makeText(requireContext(), "네트워크 연결을 확인해주세요.", Toast.LENGTH_SHORT).show()
                }
                is VerifyPhoneVerificationUiState.Idle -> {
                    // 버튼 활성화
                    binding.btnCompleted.isEnabled = true
                }
                is VerifyPhoneVerificationUiState.Loading -> {
                    // 버튼 비활성화 (중복 클릭 방지)
                    binding.btnCompleted.isEnabled = false
                }
            }
        }
    }

    override fun initView() {

        // 프로그레스 바 10% → 25% 애니메이션 적용
        binding.ivSignupProgressBar.setProgressBarFillAnimated(
            container = binding.flSignupProgressContainer,
            fromPercent = 0.1f,
            toPercent = 0.25f,
            duration = 500L
        )

        // 인증번호 받기
        binding.tvUserVerifyPhone.setOnClickListener {
            val inputPhone = binding.etUserVerifyPhone.text.toString().trim()
            Log.d("SignUpVerifyFragment", "인증번호 받기 버튼 클릭됨, 입력된 전화번호: $inputPhone")
            if (inputPhone.isNotEmpty()) {
                Log.d("SignUpVerifyFragment", "ViewModel에 전화번호 전송 요청")
                viewModel.sendPhoneVerification(inputPhone)
            } else {
                Log.w("SignUpVerifyFragment", "전화번호가 비어있음")
                Toast.makeText(requireContext(), "전화번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etUserVerifyCode.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.clUserVerifyCode.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_signup_input_bar_selected)
            } else {
                binding.clUserVerifyCode.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_signup_input_bar)
            }
        }

        binding.etUserVerifyCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val errorDrawableState = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_signup_input_bar_error
                )?.constantState

                val currentBackgroundState = binding.clUserVerifyCode.background.constantState

                if (currentBackgroundState == errorDrawableState) {
                    binding.clUserVerifyCode.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_signup_input_bar_selected)

                    binding.ivUserVerifyCodeCheckIcon.visibility = View.GONE

                    // 오류 문구 대신 타이머 텍스트 복원
                    binding.tvUserVerifyCode.text = lastTimerText
                    binding.tvUserVerifyCode.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.assu_main)
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 엔터 입력 시 인증 처리
        binding.etUserVerifyCode.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                checkVerificationCode()
                true
            } else {
                false
            }
        }

        // 인증 완료 버튼 클릭 → 다음 프래그먼트 이동
        binding.btnCompleted.setOnClickListener {
            if (isVerified) {
                // 전화번호를 SignUpViewModel에 저장
                val phoneNumber = binding.etUserVerifyPhone.text.toString().trim()
                signUpViewModel.setPhoneNumber(phoneNumber)
                findNavController().navigate(R.id.action_verify_to_type)
            }
        }
    }

    private fun startVerificationUI() {
        // 전화번호 필드 비활성화
        binding.etUserVerifyPhone.isEnabled = false

        // UI 변경
        binding.ivUserVerifyCheckIcon.isVisible = true
        binding.tvUserVerifyPhone.text = "전송완료"

        binding.clUserVerifyCode.visibility = View.VISIBLE
        binding.llQuestionCodeIsNotComing.visibility = View.VISIBLE
        binding.tvUserVerifyCode.text = "05:00"

        // 기본 상태 초기화
        binding.etUserVerifyCode.text?.clear()
        binding.etUserVerifyCode.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_signup_input_bar)
        binding.ivUserVerifyCodeCheckIcon.visibility = View.GONE
        binding.tvUserVerifyCode.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_main))
        binding.tvUserVerifyCode.text = "05:00"

        setButtonEnabled(false)
    }

    private fun startTimer() {
        countDownTimer?.cancel()

        val startTime = System.currentTimeMillis()
        timerStartTime = startTime
        timerEndTime = startTime + totalTimeMillis

        countDownTimer = object : CountDownTimer(totalTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val now = System.currentTimeMillis()
                val remainingMillis = timerEndTime - now

                if (remainingMillis > 0) {
                    val minutes = (remainingMillis / 1000) / 60
                    val seconds = (remainingMillis / 1000) % 60
                    val timeText = String.format("%02d:%02d", minutes, seconds)
                    lastTimerText = timeText
                    binding.tvUserVerifyCode.text = timeText

                    // 인증 오류 상태였던 경우 → 첫 타이머 갱신 시 오류 아이콘 제거
                    if (!isVerified) {
                        binding.ivUserVerifyCodeCheckIcon.visibility = View.GONE
                    }

                } else {
                    onFinish()
                }
            }

            override fun onFinish() {
                Toast.makeText(requireContext(), "인증 시간이 만료되었습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                resetUI()
            }
        }.start()
    }

    private fun checkVerificationCode() {
        val enteredCode = binding.etUserVerifyCode.text.toString().trim()
        val phoneNumber = binding.etUserVerifyPhone.text.toString().trim()

        // 키보드 내리기
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etUserVerifyCode.windowToken, 0)

        if (enteredCode.isNotEmpty() && phoneNumber.isNotEmpty()) {
            viewModel.verifyPhoneVerification(phoneNumber, enteredCode)
        } else {
            Toast.makeText(requireContext(), "인증번호를 입력해주세요", Toast.LENGTH_SHORT).show()
        }
    }

    private fun successVerificationUI() {
        countDownTimer?.cancel()
        isVerified = true

        // 타이머 숨기기 + 인증 완료 표시
        binding.tvUserVerifyCode.text = "인증완료"
        binding.tvUserVerifyCode.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_main))
        binding.ivUserVerifyCodeCheckIcon.setImageResource(R.drawable.ic_signup_verified)
        binding.ivUserVerifyCodeCheckIcon.visibility = View.VISIBLE

        // 입력 필드 비활성화
        binding.etUserVerifyCode.isEnabled = false
        binding.etUserVerifyCode.clearFocus()

        // 버튼 활성화
        setButtonEnabled(true)
    }

    private fun errorVerificationUI() {

        binding.tvUserVerifyCode.text = "인증오류"
        binding.tvUserVerifyCode.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_error))
        binding.ivUserVerifyCodeCheckIcon.setImageResource(R.drawable.ic_signup_verified_failed)
        binding.ivUserVerifyCodeCheckIcon.visibility = View.VISIBLE
        binding.clUserVerifyCode.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_signup_input_bar_error)

        // 버튼 비활성화
        setButtonEnabled(false)
    }

    private fun setButtonEnabled(enabled: Boolean) {
        binding.btnCompleted.isEnabled = enabled
        binding.btnCompleted.background = ContextCompat.getDrawable(
            requireContext(),
            if (enabled) R.drawable.btn_basic_selected else R.drawable.btn_basic_unselected
        )
    }

    private fun resetUI() {
        isVerified = false
        binding.etUserVerifyPhone.isEnabled = true
        binding.etUserVerifyPhone.text?.clear()
        binding.etUserVerifyCode.text?.clear()
        binding.ivUserVerifyCheckIcon.isVisible = false
        binding.tvUserVerifyPhone.text = "인증번호 받기"
        binding.clUserVerifyCode.visibility = View.GONE
        binding.llQuestionCodeIsNotComing.visibility = View.GONE
        setButtonEnabled(false)
    }

    override fun onDestroyView() {
        countDownTimer?.cancel()
        super.onDestroyView()
    }
}
