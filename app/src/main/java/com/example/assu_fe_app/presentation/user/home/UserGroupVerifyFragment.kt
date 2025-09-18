package com.example.assu_fe_app.presentation.user.home

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
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

    // ViewModel 주입은 동일
    private val viewModel: UserVerifyViewModel by activityViewModels()
    private val certificationViewModel: CertifyViewModel by activityViewModels()

    private lateinit var buttons: List<View>
    private var userIds: List<Long>? = null

    override fun initObserver() {
        // 연결 상태 관찰
        certificationViewModel.connectionStatus.observe(viewLifecycleOwner) { status ->
            // ENUM을 사용하므로 when(status)만으로 분기 처리 가능
            when (status) {
                CertifyViewModel.ConnectionStatus.CONNECTING -> showConnectionStatus("서버에 연결 중...")
                CertifyViewModel.ConnectionStatus.CONNECTED -> showConnectionStatus("연결됨 - 인증 대기 중")
                CertifyViewModel.ConnectionStatus.FAILED -> showConnectionStatus("연결 실패")
                // 재연결은 클라이언트가 자동으로 처리하므로 Fragment에서 재시도 로직 제거
                CertifyViewModel.ConnectionStatus.DISCONNECTED -> showConnectionStatus("연결 끊김")
                null -> showConnectionStatus("초기화 중...")
            }
        }

        certificationViewModel.userIds.observe(viewLifecycleOwner) { ids ->
            userIds = ids
        }

        // 현재 인증 인원 수 관찰
        certificationViewModel.currentCount.observe(viewLifecycleOwner) { count ->
            updateProgressButtons(count)
        }

        // 인증 완료 상태 관찰
        certificationViewModel.isCompleted.observe(viewLifecycleOwner) { completed ->
            if (completed) {
                onCertificationCompleted()
            }
        }

        // 에러 메시지 관찰
        certificationViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            }
        }

        // 완료 메시지 관찰
        certificationViewModel.completionMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
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

        setupInitialUI()

        // ✨ 변경점: ViewModel의 함수를 직접 호출 (토큰 전달 필요 없음)
//        certificationViewModel.subscribeToProgress(viewModel.sessionId)
        certificationViewModel.test_subscribeAndSendRequest(viewModel.sessionId, viewModel.selectedAdminId)

        generateQrCode(viewModel.sessionId, viewModel.selectedAdminId)
    }

    private fun setupInitialUI() {
        binding.btnGroupVerifyComplete.isEnabled = false
        binding.btnGroupVerifyComplete.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_basic_unselected)

        buttons.forEach { it.visibility = View.GONE }
        for (i in 0 until viewModel.selectedPeople) {
            if (i < buttons.size) {
                buttons[i].visibility = View.VISIBLE
                buttons[i].background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_basic_unselected)
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
                    storeId = viewModel.storeId,
                    tableNumber = viewModel.tableNumber,
                    adminName = viewModel.selectedAdminName,
                    contentId = viewModel.selectedContentId,
                    discount = 0L, // amount -> discount, Long 타입이므로 0L로 명시
                    partnershipContent = viewModel.selectedPaperContent, // content -> partnershipContent
                    placeName = viewModel.storeName.value.toString(), // storeName -> placeName
                    userIds = userIds ?: emptyList()
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

    private fun updateProgressButtons(count: Int) {
        for (i in 0 until buttons.size) {
            val background = if (i < count) R.drawable.btn_basic_selected else R.drawable.btn_basic_unselected
            buttons[i].background = ContextCompat.getDrawable(requireContext(), background)
        }
        if (count >= viewModel.selectedPeople) {
            enableCompleteButton()
        }
    }

    private fun onCertificationCompleted() {
        buttons.forEach {
            it.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_basic_selected)
        }
        enableCompleteButton()
        showConnectionStatus("인증 완료!")
    }

    private fun enableCompleteButton() {
        binding.btnGroupVerifyComplete.isEnabled = true
        binding.btnGroupVerifyComplete.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_basic_selected)
    }

    private fun showConnectionStatus(message: String) {
        // TODO: UI에 연결 상태를 표시할 TextView가 있다면 여기에 업데이트
        // binding.tvConnectionStatus.text = message
        Log.d("GroupVerify", "Connection Status: $message")
    }

    private fun generateQrCode(sessionId: Long, adminId: Long) {
        val qrData = "https://assu.com/verify?sessionId=$sessionId&adminId=$adminId"
        try {
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(qrData, BarcodeFormat.QR_CODE, 300, 300)
            val bitmap = BarcodeEncoder().createBitmap(bitMatrix)
            binding.ivGroupQr.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            Log.e("UserGroupVerifyFragment", "QR 코드 생성 실패", e)
            Toast.makeText(requireContext(), "QR 코드 생성에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // ViewModel이 살아있는 동안 연결을 유지해야 한다면 이 코드는 부적절할 수 있습니다.
        // 화면을 벗어날 때 항상 연결을 끊어야 한다면 이 코드를 유지합니다.
        certificationViewModel.disconnect()
    }
}