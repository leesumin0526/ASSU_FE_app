package com.assu.app.presentation.user.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.assu.app.data.local.AuthTokenLocalStore
import com.assu.app.databinding.ActivityUserQrVerifyBinding
import com.assu.app.presentation.base.BaseActivity
import com.assu.app.ui.certification.CertifyViewModel
import com.assu.app.R
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import dagger.hilt.android.AndroidEntryPoint
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class UserQRVerifyActivity :
    BaseActivity<ActivityUserQrVerifyBinding>(R.layout.activity_user_qr_verify) {
    private lateinit var cameraExecutor: ExecutorService
    private var qrCodeScannedSuccessfully = false // TODO QR 인식 성공 여부 플래그 ( 에뮬레이터에는 임시로 true 로 두기)
    private var isAnalyzing = true // 분석 상태를 제어하는 플래그
    private val CAMERA_PERMISSION_CODE = 100
    private var qrCodeData: String? = null

    @Inject
    lateinit var infoManager : AuthTokenLocalStore

    @Inject
    lateinit var tokenProvider : AuthTokenLocalStore
    private val certifyViewModel: CertifyViewModel by viewModels()

    override fun initView() {
        applyWindowInsetPadding()

        binding.previewView.post {
            binding.overlay.updateHoleRectFromView(binding.qrGuideBox)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        // '확인' 버튼은 처음에는 비활성화 상태입니다.
        setConfirmButtonState(false) // 초기에는 비활성화

        binding.btnConfirm.setOnClickListener { // 이미지뷰 클릭 리스너
            Log.d("UserQRVerifyActivity","클릭했음.")
            if (qrCodeScannedSuccessfully) {

                Log.d("UserQRVerifyActivity", "showNextFragment() 가 호출됩니다. ")
                // 다음 프래그먼트로 전환
                showNextFragment()
            }
        }


        binding.tvUniversity.text = infoManager.getBasicInfoUniversity()
        binding.tvDepartment.text = infoManager.getBasicInfoDepartment()
// TODO 나중에 주석해제
        cameraExecutor = Executors.newSingleThreadExecutor()
        checkCameraPermission()
//        onEmulatorScanSuccess() // 에뮬레이터 용
    }

    override fun initObserver() {

    }

    private fun onEmulatorScanSuccess() {
        qrCodeData = "https://assu.com/verify?storeId=7" // TODO 여기 ...
        Log.d("QR 인식 성공", "에뮬레이터 테스트용 데이터 사용: $qrCodeData")
        binding.tvQrInstruction.text = "QR 코드를 성공적으로 인식했습니다."
        setConfirmButtonState(true)
        binding.fragmentContainerView.visibility = View.VISIBLE
        qrCodeScannedSuccessfully = true

        showNextFragment()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            startCamera()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, QrCodeAnalyzer { qrCode ->
                        if (!qrCodeScannedSuccessfully && isAnalyzing) { // isAnalyzing 플래그 추가
                            runOnUiThread {
                                isAnalyzing = false // 분석 중단
                                qrCodeData = qrCode
//                                qrCodeData = "https://assu.com/verify?sessionId=7&adminId=2"
                                Log.d("QR 인식 성공", "성공했다네요? $qrCode")
                                binding.tvQrInstruction.text = "QR 코드를 성공적으로 인식했습니다."
                                setConfirmButtonState(true) // '확인' 버튼 활성화
                                qrCodeScannedSuccessfully = true // 플래그 설정

                                // 카메라 즉시 해제
                                cameraProvider.unbindAll()

                                showNextFragment()
                            }
                        }
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )
            } catch (e: Exception) {
                Log.e("CameraX", "카메라 바인딩 실패", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            finish()
        }
    }

    // '확인' 버튼의 활성화 상태를 제어하는 함수
    private fun setConfirmButtonState(isEnabled: Boolean) {
        if (isEnabled) {
            binding.btnConfirm.alpha = 1.0f
            binding.tvConfirm.alpha = 1.0f
            binding.btnConfirm.isClickable = true
            binding.tvConfirm.isClickable = true
        } else {
            binding.btnConfirm.alpha = 0.3f
            binding.tvConfirm.alpha = 0.3f
            binding.btnConfirm.isClickable = false
            binding.tvConfirm.isClickable = false
        }
    }

    // `BaseActivity`에 없는 함수들을 여기에 정의
    private fun applyWindowInsetPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val extraPaddingTop = 3 // 추가 padding (dp)
            v.setPadding(
                systemBars.left,
                systemBars.top + extraPaddingTop.dpToPx(v.context),
                systemBars.right,
                0
            )
            insets
        }
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    private fun showNextFragment() {
        Log.d("UserQRVerifyActivity", "showNextFragment() 가 호출되었습니다.")
        // QR 코드 데이터 파싱 수정
        val result = qrCodeData?.let { data ->
            when {
                data.contains("storeId=") -> {
                    "storeId" to data.split("storeId=").lastOrNull()?.toLong()
                }
                data.contains("sessionId=") && data.contains("adminId=") -> {
                    // URL 파싱 수정: & 기준으로 분리
                    val sessionId = extractParameterFromUrl(data, "sessionId")
                    val adminId = extractParameterFromUrl(data, "adminId")
                    "sessionIdAndAdminId" to Pair(sessionId, adminId)
                }
                else -> null
            }
        }

        val type = result?.first
        val idValue = result?.second

        when (type) {
            "storeId" -> {
                // 대표자 역할: 매장 선택으로 이동
                Log.d("UserQRVerifyActivity"," 대표자 역할인 것을 확인하였습니다.")
                handleStoreOwnerFlow(idValue as Long)
            }
            "sessionIdAndAdminId" -> {
                Log.d("UserQRVerifyActivity", "그룹인증인 것을 확인하였습니다.")
                // 인증 요청자 역할: 그룹 인증 시작
                val (sessionId, adminId) = idValue as Pair<Long?, Long?>
                if (sessionId != null && adminId != null) {
                    handleCertificationRequesterFlow(sessionId, adminId)
                    observeCertificationStates(sessionId)
                } else {
                    showInvalidQrError()
                }
            }
            else -> {
                showInvalidQrError()
            }
        }
    }

    // URL에서 파라미터 추출하는 헬퍼 함수 추가
    private fun extractParameterFromUrl(url: String, paramName: String): Long? {
        return try {
            val regex = "$paramName=(\\d+)".toRegex()
            val matchResult = regex.find(url)
            matchResult?.groupValues?.get(1)?.toLong()
        } catch (e: Exception) {
            Log.e("URL_PARSE", "Failed to extract $paramName from $url", e)
            null
        }
    }

    // 대표자 플로우: 매장 정보로 이동 (수정 없음)
    private fun handleStoreOwnerFlow(storeId: Long) {
        Log.d("UserQRVerifyActivity", "곧 테이블 화면으로 전환됩니다. ")
        val fragment = UserTableNumberSelectFragment().apply {
            arguments = Bundle().apply {
                putLong("storeId", storeId)
            }
        }
        binding.fragmentContainerView.visibility = View.VISIBLE

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .addToBackStack(null)
            .commit()
    }

    // 인증 요청자 플로우: 수정 필요
    private fun handleCertificationRequesterFlow(sessionId: Long, adminId: Long) {
        // 로딩 상태 표시
        showCertificationLoadingState()
        // TODO : WebSocket 연결 및 인증 요청 - 임시 주석 처리
        certifyViewModel.connectAndCertify(sessionId, adminId){
            onCertificationCompleted(sessionId)
        }

    }

    private fun observeCertificationStates(sessionId: Long) {
        // 연결 상태 관찰
        certifyViewModel.connectionStatus.observe(this) { status ->
            when (status) {
                CertifyViewModel.ConnectionStatus.CONNECTING -> {
                    updateLoadingMessage("서버에 연결 중...")
                }
                CertifyViewModel.ConnectionStatus.CONNECTED -> {
                    updateLoadingMessage("인증 요청 전송 중...")
                }
                CertifyViewModel.ConnectionStatus.FAILED -> {
                    showConnectionFailedState()
                }
                CertifyViewModel.ConnectionStatus.DISCONNECTED -> {
                    updateLoadingMessage("연결이 끊어졌습니다.")
                }
            }
        }

        // 인증 완료 상태 관찰
        certifyViewModel.isCompleted.observe(this) { completed ->
            if (completed) {
                onCertificationCompleted(sessionId)
            }
        }

        // 진행 상황 관찰 (선택사항)
        certifyViewModel.currentCount.observe(this) { count ->
            updateLoadingMessage("인증 대기 중... (현재 $count 명 참여)")
        }

        // 에러 메시지 관찰
        certifyViewModel.errorMessage.observe(this) { error ->
            if (error.isNotEmpty()) {
                showCertificationError(error)
            }
        }
    }

    private fun onCertificationCompleted(sessionId: Long) {
        hideCertificationLoadingState()

        // 완료 화면으로 이동
        val fragment = UserPartnershipVerifyCompleteFragment().apply {
            arguments = Bundle().apply {
                putLong("sessionId", sessionId)
            }
        }

        binding.fragmentContainerView.visibility = View.VISIBLE


        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .addToBackStack(null)
            .commit()
    }

    // UI 상태 관리 함수들
    private fun showCertificationLoadingState() {
        binding.tvQrInstruction.text = "그룹 인증을 시작합니다..."
        setConfirmButtonState(false)
    }

    private fun updateLoadingMessage(message: String) {
        binding.tvQrInstruction.text = message
    }

    private fun hideCertificationLoadingState() {
        // binding.progressBar.visibility = View.GONE
    }

    private fun showConnectionFailedState() {
        binding.tvQrInstruction.text = "서버 연결에 실패했습니다."
        setConfirmButtonState(true)

        // 확인 버튼을 재시도 버튼으로 변경
        binding.tvConfirm.text = "재시도"

        // 기존 클릭 리스너 제거하고 재시도 리스너 설정
        binding.btnConfirm.setOnClickListener {
            retryConnection()
        }
        binding.tvConfirm.setOnClickListener {
            retryConnection()
        }
    }

    private fun retryConnection() {
        // 재시도 로직
        val sessionId = certifyViewModel.sessionId.value
        val qrResult = qrCodeData?.let { data ->
            if (data.contains("adminId=")) {
                extractParameterFromUrl(data, "adminId")
            } else null
        }

        if (sessionId != null && qrResult != null) {
            // 버튼 텍스트 원래대로 복구
            binding.tvConfirm.text = "확인"
            handleCertificationRequesterFlow(sessionId, qrResult)
        } else {
            finish()
            startActivity(intent) // 액티비티 재시작
        }
    }


    private fun showCertificationError(error: String) {
        hideCertificationLoadingState()

        // 재시도 옵션 제공
        binding.tvQrInstruction.text = "인증에 실패했습니다."
        setConfirmButtonState(true)
        binding.tvConfirm.text = "재시도"

        binding.btnConfirm.setOnClickListener {
            retryConnection()
        }
        binding.tvConfirm.setOnClickListener {
            retryConnection()
        }
    }

    private fun showInvalidQrError() {
        finish()
        startActivity(intent)
    }

    // Activity 종료 시 WebSocket 연결 해제
    override fun onDestroy() {
        super.onDestroy()
        isAnalyzing = false // 분석 중단

        // cameraExecutor가 초기화된 경우에만 종료
        if (::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
        }

        // WebSocket 연결 해제
        certifyViewModel.disconnect()
    }

    // 이미지 프레임 분석을 위한 클래스
    private inner class QrCodeAnalyzer(private val onQrCodeScanned: (String) -> Unit) : ImageAnalysis.Analyzer {
        private val reader = MultiFormatReader().apply {
            setHints(mapOf(
                DecodeHintType.POSSIBLE_FORMATS to arrayListOf(BarcodeFormat.QR_CODE)
            ))
        }

        override fun analyze(image: ImageProxy) {
            // 분석 중단 플래그 체크
            if (!isAnalyzing) {
                image.close()
                return
            }

            val rotationDegrees = image.imageInfo.rotationDegrees
            val buffer = image.planes[0].buffer
            val bytes = buffer.toByteArray()
            val source = PlanarYUVLuminanceSource(
                bytes,
                image.width,
                image.height,
                0,
                0,
                image.width,
                image.height,
                false
            )

            try {
                val bitmap = BinaryBitmap(HybridBinarizer(source))
                val result = reader.decodeWithState(bitmap)
                onQrCodeScanned(result.text)
            } catch (e: Exception) {
                // 분석 중인 경우에만 로그 출력
                if (isAnalyzing) {
                    Log.d("QR_SCANNER", "QR 코드 인식 실패", e)
                }
            } finally {
                image.close()
                reader.reset()
            }
        }

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()
            val data = ByteArray(remaining())
            get(data)
            return data
        }
    }
}