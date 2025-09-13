package com.example.assu_fe_app.presentation.user.home

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.DevBearerInterceptor
import com.example.assu_fe_app.data.dto.usage.SaveUsageRequestDto
import com.example.assu_fe_app.databinding.FragmentUserGroupVerifyBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.ui.certification.CertifyViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder

class UserGroupVerifyFragment : BaseFragment<FragmentUserGroupVerifyBinding>(R.layout.fragment_user_group_verify) {

    private val viewModel: UserVerifyViewModel by activityViewModels()
    private val certificationViewModel: CertifyViewModel by activityViewModels()
    private var qrCodeImageBitmap: Bitmap? = null

    private lateinit var buttons: List<View>
    private var userIds : List<Long>? = null

    override fun initObserver() {
        // 연결 상태 관찰
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

        certificationViewModel.userIds.observe(this){ ids ->
            userIds = ids
        }

        // 현재 인증 인원 수 관찰
        certificationViewModel.currentCount.observe(this) { count ->
            updateProgressButtons(count)
        }

        // 인증 완료 상태 관찰
        certificationViewModel.isCompleted.observe(this) { completed ->
            if (completed) {
                onCertificationCompleted()
            }
        }

        // 에러 메시지 관찰
        certificationViewModel.errorMessage.observe(this) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            }
        }

        // 완료 메시지 관찰
        certificationViewModel.completionMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun initView() {

        buttons = listOf(
            binding.groupVerify1,
            binding.groupVerify2,
            binding.groupVerify3,
            binding.groupVerify4
        )

        binding.tvGroupMarketName.text = viewModel.storeName.value
        binding.tvGroupPartnershipContent.text = viewModel.selectedPaperContent

        // 초기 버튼 상태 설정
        setupInitialUI()

        // WebSocket 연결 시작
        connectToWebSocket()

        // QR 코드 생성
        generateQrCode(viewModel.sessionId, viewModel.selectedAdminId)
    }

    private fun setupInitialUI() {
        // 완료 버튼 초기에는 비활성화
        binding.btnGroupVerifyComplete.isEnabled = false
        binding.btnGroupVerifyComplete.background = resources.getDrawable(R.drawable.btn_basic_unselected, null)

        // 선택된 인원 수만큼 버튼 표시
        buttons.forEach { it.visibility = View.GONE }
        for (i in 0 until viewModel.selectedPeople) {
            if (i < buttons.size) {
                buttons[i].visibility = View.VISIBLE
                // 초기에는 모든 버튼을 비활성 상태로
                buttons[i].background = resources.getDrawable(R.drawable.btn_basic_unselected, null)
            }
        }

        binding.btnGroupVerifyComplete.setOnClickListener {
            if(viewModel.isGoodsList){
                val fragment = UserSelectServiceFragment()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container_view, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            } else{

                certificationViewModel.saveGroupUsage(
                    SaveUsageRequestDto(
                        viewModel.storeId,
                        viewModel.tableNumber,
                        viewModel.selectedAdminName,
                        viewModel.selectedContentId,
                        0 ,
                        viewModel.selectedPaperContent,
                        viewModel.storeName.toString(),
                        userIds?: emptyList()
                    )
                )
                val fragment = UserPartnershipVerifyCompleteFragment()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container_view, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }

        }

        binding.btnGroupBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun connectToWebSocket() {
        val authToken = getAuthToken()
        if (authToken.isEmpty()) {
            Log.d("UserGroupVerifyFragment", "토큰이 들어오지 않았습니다")
            return
        }

        // 대표자용 WebSocket 연결 (구독만 하고 인증 요청은 하지 않음)
        certificationViewModel.subscribeToProgress(viewModel.sessionId, authToken)
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
        if (count >= viewModel.selectedPeople) {
            enableCompleteButton()
        }
    }

    private fun onCertificationCompleted() {
        // 모든 버튼 활성화
        buttons.forEach {
            it.background = resources.getDrawable(R.drawable.btn_basic_selected, null)
        }


        enableCompleteButton()

        // 완료 상태 UI 업데이트
        showConnectionStatus("인증 완료!")
    }

    private fun enableCompleteButton() {
        binding.btnGroupVerifyComplete.isEnabled = true
        binding.btnGroupVerifyComplete.background = resources.getDrawable(R.drawable.btn_basic_selected, null)
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
        return "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdXRoUmVhbG0iOiJTU1UiLCJyb2xlIjoiU1RVREVOVCIsInVzZXJJZCI6NiwidXNlcm5hbWUiOiIyMDI0MTY5MyIsImp0aSI6IjFlYzM4YmViLTNkNjMtNDBmYi05MGYzLTliOTM2MDI5N2I2NCIsImlhdCI6MTc1NzU5ODU5OCwiZXhwIjoxNzU3NjAyMTk4fQ.WMsFOqLnhvJ2Up_SeP2cqjJl6-ZOF0vFcXGJDxYZOCI"
    }

    private fun generateQrCode(sessionId: Long, adminId: Long) {
        val qrData = "https://assu.com/verify?sessionId=$sessionId&adminId=$adminId"
        Log.d("QR 생성", "생성된 QR 데이터: $qrData")

        try {
            val multiFormatWriter = MultiFormatWriter()
            val bitMatrix: BitMatrix = multiFormatWriter.encode(
                qrData,
                BarcodeFormat.QR_CODE,
                300,
                300
            )

            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            qrCodeImageBitmap = bitmap

            requireActivity().runOnUiThread {
                binding.ivGroupQr.setImageBitmap(bitmap)
            }

        } catch (e: WriterException) {
            Log.e("UserGroupVerifyFragment", "QR 코드 생성 실패", e)
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "QR 코드 생성에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Fragment 종료 시 WebSocket 연결 해제
        certificationViewModel.disconnect()
    }
}