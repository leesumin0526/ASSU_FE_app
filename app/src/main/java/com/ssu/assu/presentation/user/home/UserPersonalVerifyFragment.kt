package com.ssu.assu.presentation.user.home

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.ssu.assu.R
import com.ssu.assu.databinding.FragmentUserPersonalVerifyBinding
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.ui.certification.CertifyViewModel
import kotlin.getValue


class UserPersonalVerifyFragment : BaseFragment<FragmentUserPersonalVerifyBinding>(R.layout.fragment_user_personal_verify) {
    // 오직 테스트 용
    private val buttons = listOf(
        binding.groupVerify1,
        binding.groupVerify2,
        binding.groupVerify3,
        binding.groupVerify4
    )

    private val certificationViewModel: CertifyViewModel by activityViewModels()


    override fun initObserver() {
        certificationViewModel.connectionStatus.observe(this) { status ->
            when (status) {
                CertifyViewModel.ConnectionStatus.CONNECTING -> {
                    showConnectionStatus("서버에 연결 중...")
                }
                CertifyViewModel.ConnectionStatus.CONNECTED -> {
                    showConnectionStatus("연결됨 - 인증 대기 중")
                }
                CertifyViewModel.ConnectionStatus.FAILED -> {
                    showConnectionStatus("연결 실패")
                    showRetryOption()
                }
                CertifyViewModel.ConnectionStatus.DISCONNECTED -> {
                    showConnectionStatus("연결 끊김")
                }
            }
        }

        // 현재 인증 인원 수 관찰
        certificationViewModel.currentCount.observe(this) { count ->
            updateProgressButtons(count)
        }
    }

    override fun initView() {

        connectToWebSocket()
        // 모두 인증이 완료 되었을때 btnPersonalVerifyComplete의 색을 활성화 해줘야 됨.


    }

    private fun connectToWebSocket() {
        val authToken = getAuthToken()
        if (authToken.isEmpty()) {
            Log.d("토큰 없음❌", "토큰이 들어오지 않았습니다")
            return
        }

        // 대표자용 WebSocket 연결 (구독만 하고 인증 요청은 하지 않음)
        certificationViewModel.subscribeToProgress(7)
    }

    private fun updateProgressButtons(count: Int) {
        // count만큼 버튼을 활성화 상태로 변경
        for (i in 0 until buttons.size) {
            if (i < count) {
                buttons[i].background = resources.getDrawable(R.drawable.btn_basic_selected, null)
            } else {
                buttons[i].background = resources.getDrawable(R.drawable.btn_basic_unselected, null)
            }
        }

        // 목표 인원에 도달했는지 확인
        if (count >= 3 ) { //임시 값
            enableCompleteButton()
        }
    }

    private fun enableCompleteButton() {
        binding.btnPersonalVerifyComplete.isEnabled = true
        binding.btnPersonalVerifyComplete.background = resources.getDrawable(R.drawable.btn_basic_selected, null)
    }

    private fun showConnectionStatus(message: String) {
        // 연결 상태를 표시할 TextView가 있다면 업데이트
        // binding.tvConnectionStatus?.text = message
        Log.d("GroupVerify", "Connection Status: $message")
    }

    private fun showRetryOption() {
        // 재연결 버튼을 보여주거나 자동 재연결 시도
        Toast.makeText(requireContext(), "연결에 실패했습니다. 재시도 중...", Toast.LENGTH_SHORT).show()

        // 3초 후 재연결 시도
        binding.root.postDelayed({
            connectToWebSocket()
        }, 3000)
    }

    private fun getAuthToken(): String {
//        val sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
//        return sharedPref.getString("token", "") ?: ""
        return "Bearer "
    }




}