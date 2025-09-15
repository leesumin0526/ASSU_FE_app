package com.example.assu_fe_app.presentation.common.signup

import android.content.Context
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentSignUpVerifyBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.util.setProgressBarFillAnimated

class SignUpVerifyFragment :
    BaseFragment<FragmentSignUpVerifyBinding>(R.layout.fragment_sign_up_verify) {

    private var countDownTimer: CountDownTimer? = null
    private val totalTimeMillis = 5 * 60 * 1000L // 5분
    private var isVerified = false
    // 임시 허용
    private val correctPhoneNumber = "01012345678"
    private val correctVerificationCode = "1234"

    private var lastTimerText: String = "05:00"

    private var timerStartTime: Long = 0L
    private var timerEndTime: Long = 0L

    override fun initObserver() {}

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
            findNavController().navigate(R.id.action_toTest)
//            val inputPhone = binding.etUserVerifyPhone.text.toString()
//            if (inputPhone == correctPhoneNumber) {
//                startVerificationUI()
//                startTimer()
//            } else {
//                Toast.makeText(requireContext(), "올바르지 않은 전화번호입니다", Toast.LENGTH_SHORT).show()
//            }
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

        // 키보드 내리기
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etUserVerifyCode.windowToken, 0)

        if (enteredCode == correctVerificationCode) {
            successVerificationUI()
        } else {
            errorVerificationUI()
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
