package com.assu.app.presentation.common.signup

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
import com.assu.app.R
import com.assu.app.databinding.FragmentSignUpVerifyBinding
import com.assu.app.presentation.base.BaseFragment
import com.assu.app.ui.auth.SignUpVerifyViewModel
import com.assu.app.ui.auth.SignUpVerifyViewModel.SendPhoneVerificationUiState
import com.assu.app.ui.auth.SignUpVerifyViewModel.VerifyPhoneVerificationUiState
import com.assu.app.ui.auth.SignUpViewModel
import com.assu.app.util.setProgressBarFillAnimated
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
    
    // 포커스 해제를 위한 Runnable
    private val focusClearRunnable = Runnable {
        Log.d("SignUpVerifyFragment", "Auto clearing focus after 2 seconds")
        binding.etUserVerifyCode.clearFocus()
    }
    
    // 입력 중인지 확인하는 플래그
    private var isTyping = false

    private var timerStartTime: Long = 0L
    private var timerEndTime: Long = 0L
    
    // 토스트 메시지 중복 방지를 위한 플래그
    private var isShowingToast = false
    
    // API 호출 중복 방지를 위한 플래그
    private var isVerificationInProgress = false

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
                    
                    // 토스트 메시지 중복 방지
                    if (!isShowingToast) {
                        isShowingToast = true
                        Toast.makeText(requireContext(), state.message ?: "인증번호 발송에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        // 토스트 표시 후 플래그 초기화
                        binding.root.postDelayed({ isShowingToast = false }, 3000)
                    }
                    
                    // 400, 409 에러 시 UI 업데이트 (빨간색 표시)
                    if (state.code == 400 || state.code == 409) {
                        showPhoneNumberError(state.message)
                    }
                }
                is SendPhoneVerificationUiState.Error -> {
                    Log.d("SignUpVerifyViewModel", "네트워크 에러: ${state.message}")
                    // 버튼 재활성화
                    binding.tvUserVerifyPhone.isEnabled = true
                    // 토스트 메시지 중복 방지
                    if (!isShowingToast) {
                        isShowingToast = true
                        Toast.makeText(requireContext(), state.message ?: "네트워크 연결을 확인해주세요.", Toast.LENGTH_SHORT).show()
                        // 토스트 표시 후 플래그 초기화
                        binding.root.postDelayed({ isShowingToast = false }, 3000)
                    }
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
            Log.d("SignUpVerifyFragment", "Verification state changed: $state")
            when (state) {
                is VerifyPhoneVerificationUiState.Success -> {
                    isVerificationInProgress = false
                    successVerificationUI()
                }
                is VerifyPhoneVerificationUiState.Fail -> {
                    isVerificationInProgress = false
                    Log.d("SignUpVerifyFragment", "인증번호 검증 실패: code=${state.code}, message=${state.message}")
                    errorVerificationUI()
                    // 버튼 재활성화
                    binding.btnCompleted.isEnabled = true
                    // 토스트 메시지 중복 방지
                    if (!isShowingToast) {
                        isShowingToast = true
                        Toast.makeText(requireContext(), "인증번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                        // 토스트 표시 후 플래그 초기화
                        binding.root.postDelayed({ isShowingToast = false }, 3000)
                    }
                }
                is VerifyPhoneVerificationUiState.Error -> {
                    isVerificationInProgress = false
                    errorVerificationUI()
                    Log.d("SignUpVerifyViewModel", "네트워크 에러: ${state.message}")
                    // 버튼 재활성화
                    binding.btnCompleted.isEnabled = true
                    // 토스트 메시지 중복 방지
                    if (!isShowingToast) {
                        isShowingToast = true
                        Toast.makeText(requireContext(), "네트워크 연결을 확인해주세요.", Toast.LENGTH_SHORT).show()
                        // 토스트 표시 후 플래그 초기화
                        binding.root.postDelayed({ isShowingToast = false }, 3000)
                    }
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

        // 인증번호 입력 필드 최대 길이 제한 (6자리)
        binding.etUserVerifyCode.filters = arrayOf(android.text.InputFilter.LengthFilter(6))
        Log.d("SignUpVerifyFragment", "Set max length to 6 characters")

        // 부모 뷰 클릭 시 EditText 포커스 해제
        binding.root.setOnClickListener {
            Log.d("SignUpVerifyFragment", "Root view clicked - clearing focus")
            binding.etUserVerifyCode.clearFocus()
            binding.etUserVerifyPhone.clearFocus()
            
            // 키보드 숨기기
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        }

        // 전화번호 입력 필드 텍스트 변경 감지
        binding.etUserVerifyPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // 전화번호가 변경되면 에러 상태 초기화
                resetPhoneNumberError()
            }
        })

        // 인증번호 받기
        binding.tvUserVerifyPhone.setOnClickListener {
//            findNavController().navigate(R.id.action_verify_to_info)
            val inputPhone = binding.etUserVerifyPhone.text.toString().trim()
            Log.d("SignUpVerifyFragment", "인증번호 받기 버튼 클릭됨, 입력된 전화번호: $inputPhone")
            if (inputPhone.isNotEmpty()) {
                Log.d("SignUpVerifyFragment", "ViewModel에 전화번호 전송 요청")
                viewModel.checkAndSendPhoneVerification(inputPhone)
            } else {
                Log.w("SignUpVerifyFragment", "전화번호가 비어있음")
                Toast.makeText(requireContext(), "전화번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etUserVerifyCode.setOnFocusChangeListener { _, hasFocus ->
            Log.d("SignUpVerifyFragment", "Focus change: hasFocus=$hasFocus, isVerified=$isVerified")
            if (hasFocus) {
                binding.clUserVerifyCode.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_signup_input_bar_selected)
            } else {
                binding.clUserVerifyCode.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_signup_input_bar)
                
                // 포커스를 잃을 때 인증번호가 6자리 이상이고 아직 인증되지 않았다면 검증 API 호출
                val enteredCode = binding.etUserVerifyCode.text.toString().trim()
                Log.d("SignUpVerifyFragment", "Focus lost - enteredCode: '$enteredCode', length: ${enteredCode.length}, isVerified: $isVerified, isTyping: $isTyping")
                
                // 6자리 이상이고, 타이핑 중이 아니며, 아직 인증되지 않았을 때만 API 호출
                if (enteredCode.length >= 6 && !isVerified && !isTyping) {
                    Log.d("SignUpVerifyFragment", "Calling checkVerificationCode() from focus out")
                    checkVerificationCode()
                } else {
                    Log.d("SignUpVerifyFragment", "Skipping verification - code length: ${enteredCode.length}, already verified: $isVerified, or still typing: $isTyping")
                }
            }
        }

        // EditText 터치 시 포커스 해제를 위한 터치 리스너
        binding.etUserVerifyCode.setOnTouchListener { _, event ->
            Log.d("SignUpVerifyFragment", "EditText touched - action: ${event.action}")
            // 터치 이벤트를 처리하지 않고 부모로 전달하여 포커스 해제 유도
            false
        }

        binding.etUserVerifyCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 입력 시작 시 타이핑 플래그 설정
                isTyping = true
                Log.d("SignUpVerifyFragment", "Typing started - isTyping: $isTyping")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 입력 중에는 API 호출하지 않음
                Log.d("SignUpVerifyFragment", "Text changed - isTyping: $isTyping, text: '$s'")
                
                // 기존 타이머 제거
                binding.etUserVerifyCode.removeCallbacks(focusClearRunnable)
                
                // 텍스트가 변경되면 항상 에러 상태 초기화
                resetVerificationErrorState()
            }

            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString()?.trim() ?: ""
                Log.d("SignUpVerifyFragment", "afterTextChanged - text: '$text', length: ${text.length}")
                
                // 기존 타이머 제거
                binding.etUserVerifyCode.removeCallbacks(focusClearRunnable)
                
                // 6자리 이상일 때만 검증 API 호출
                if (text.length >= 6) {
                    Log.d("SignUpVerifyFragment", "Verification code complete - text length: ${text.length}")
                    isTyping = false
                    // 6자리 이상 입력 시 검증 API 호출
                    binding.etUserVerifyCode.postDelayed({
                        Log.d("SignUpVerifyFragment", "Calling verification for complete code")
                        checkVerificationCode()
                    }, 500) // 0.5초 후 검증
                } else {
                    // 6자리 미만일 때는 타이핑 플래그를 false로 설정하여 focus out에서도 API 호출하지 않도록 함
                    isTyping = false
                    Log.d("SignUpVerifyFragment", "Incomplete code - length: ${text.length}, no API call, isTyping set to false")
                }
            }
        })

        // 엔터 입력 시 인증 처리
        binding.etUserVerifyCode.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val enteredCode = binding.etUserVerifyCode.text.toString().trim()
                Log.d("SignUpVerifyFragment", "Enter pressed - code length: ${enteredCode.length}")
                
                // 6자리 이상일 때만 검증
                if (enteredCode.length >= 6) {
                    isTyping = false
                    Log.d("SignUpVerifyFragment", "Enter pressed - calling verification")
                    checkVerificationCode()
                } else {
                    Log.d("SignUpVerifyFragment", "Enter pressed - code too short, no verification")
                }
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

        // UI 변경 - 성공시와 동일한 위치에 체크 아이콘 표시
        binding.ivUserVerifyCheckIcon.isVisible = true
        binding.tvUserVerifyPhone.text = "전송완료"
        binding.tvUserVerifyPhone.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_main))
        binding.etUserVerifyPhone.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_signup_input_bar)

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
        // API 호출 중복 방지
        if (isVerificationInProgress) {
            Log.d("SignUpVerifyFragment", "⚠️ API 호출 이미 진행 중 - 중복 호출 방지")
            return
        }
        
        val enteredCode = binding.etUserVerifyCode.text.toString().trim()
        val phoneNumber = binding.etUserVerifyPhone.text.toString().trim()

        Log.d("SignUpVerifyFragment", "=== checkVerificationCode() called ===")
        Log.d("SignUpVerifyFragment", "🔍 API 전송 데이터:")
        Log.d("SignUpVerifyFragment", "   📱 전화번호: '$phoneNumber'")
        Log.d("SignUpVerifyFragment", "   🔢 인증번호: '$enteredCode'")
        Log.d("SignUpVerifyFragment", "   📏 인증번호 길이: ${enteredCode.length}")

        // 키보드 내리기
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etUserVerifyCode.windowToken, 0)

        if (enteredCode.isNotEmpty() && phoneNumber.isNotEmpty()) {
            Log.d("SignUpVerifyFragment", "✅ API 호출 조건 만족 - verifyPhoneVerification() 호출")
            isVerificationInProgress = true
            // 이전 상태 초기화 (중복 토스트 방지)
            viewModel.resetVerificationState()
            viewModel.verifyPhoneVerification(phoneNumber, enteredCode)
        } else {
            Log.w("SignUpVerifyFragment", "❌ API 호출 조건 불만족")
            Log.w("SignUpVerifyFragment", "   전화번호 비어있음: ${phoneNumber.isEmpty()}")
            Log.w("SignUpVerifyFragment", "   인증번호 비어있음: ${enteredCode.isEmpty()}")
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

    private fun showPhoneNumberError(message: String? = null) {
        // 전화번호 입력 필드 빨간색 표시
        binding.tvUserVerifyPhone.text = message ?: "전화번호 오류"
        binding.tvUserVerifyPhone.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_error))
        binding.ivUserVerifyCheckIcon.setImageResource(R.drawable.ic_signup_verified_failed)
        binding.ivUserVerifyCheckIcon.visibility = View.VISIBLE
        binding.clUserVerifyPhone.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_signup_input_bar_error)

        // 버튼 비활성화
        setButtonEnabled(false)
    }

    private fun resetPhoneNumberError() {
        // 전화번호 입력 필드 상태 초기화
        binding.etUserVerifyPhone.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_signup_input_bar)
        binding.clUserVerifyPhone.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_signup_input_bar)
        binding.tvUserVerifyPhone.text = "인증번호 받기"
        binding.ivUserVerifyCheckIcon.setImageResource(R.drawable.ic_signup_verified)
        binding.ivUserVerifyCheckIcon.visibility = View.GONE
        binding.tvUserVerifyPhone.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_main))
    }

    private fun resetVerificationErrorState() {
        // 인증번호 입력 필드의 에러 상태 초기화
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
        
        // 전화번호 입력 필드 상태 초기화
        binding.etUserVerifyPhone.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_signup_input_bar)
        binding.tvUserVerifyPhone.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_main))
    }


    override fun onPause() {
        super.onPause()
        // 화면이 일시정지될 때 포커스 해제
        binding.etUserVerifyCode.clearFocus()
        binding.etUserVerifyPhone.clearFocus()
    }

    override fun onDestroyView() {
        countDownTimer?.cancel()
        binding.etUserVerifyCode.removeCallbacks(focusClearRunnable)
        isTyping = false
        isVerificationInProgress = false
        isShowingToast = false
        super.onDestroyView()
    }
}
