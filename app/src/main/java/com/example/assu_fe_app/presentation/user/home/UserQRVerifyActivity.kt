package com.example.assu_fe_app.presentation.user.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings.Global.putString
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.ActivityUserQrVerifyBinding
import com.example.assu_fe_app.presentation.base.BaseActivity
import com.example.assu_fe_app.presentation.user.home.UserTableNumberSelectFragment
import com.example.assu_fe_app.ui.certification.CertifyViewModel
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import dagger.hilt.android.AndroidEntryPoint
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.getValue

@AndroidEntryPoint
class UserQRVerifyActivity :
    BaseActivity<ActivityUserQrVerifyBinding>(R.layout.activity_user_qr_verify) {

//    private lateinit var cameraExecutor: ExecutorService
    private var qrCodeScannedSuccessfully = true // QR 인식 성공 여부 플래그 ( 에뮬레이터에는 임시로 true 로 두기)
    private val CAMERA_PERMISSION_CODE = 100
    private var qrCodeData: String? = null
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
            if (qrCodeScannedSuccessfully) {
                // QR 인식이 성공했을 때만 다음으로 넘어감
                Toast.makeText(this, "인증이 완료되었습니다.", Toast.LENGTH_SHORT).show()

                // 다음 프래그먼트로 전환
                showNextFragment()
            }
        }

        binding.tvConfirm.setOnClickListener { // 텍스트뷰 클릭 리스너
            if (qrCodeScannedSuccessfully) {
                Toast.makeText(this, "인증이 완료되었습니다.", Toast.LENGTH_SHORT).show()


                // 다음 프래그먼트로 전환
                showNextFragment()
            }
        }

        binding.tvUniversity.text = "숭실대학교 학생"
        binding.tvDepartment.text = "IT대학"

//        cameraExecutor = Executors.newSingleThreadExecutor()
//        checkCameraPermission()
        onEmulatorScanSuccess()

    }

    override fun initObserver() {
        // 이 액티비티에서는 따로 LiveData를 관찰하지 않으므로 비워둡니다.
    }


    private fun onEmulatorScanSuccess() {
        qrCodeData = "https://assu.com/verify?storeId=2"
        Log.d("QR 인식 성공", "에뮬레이터 테스트용 데이터 사용: $qrCodeData")
        binding.tvQrInstruction.text = "QR 코드를 성공적으로 인식했습니다."
        setConfirmButtonState(true)
        qrCodeScannedSuccessfully = true

    }

    // 에뮬레이터 테스트 시 임의로 ..
//    private fun checkCameraPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//            != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.CAMERA),
//                CAMERA_PERMISSION_CODE
//            )
//        } else {
//            startCamera()
//        }
//    }
//
//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//
//            val preview = Preview.Builder().build().also {
//                it.setSurfaceProvider(binding.previewView.surfaceProvider)
//            }
//
//
//
//            val imageAnalyzer = ImageAnalysis.Builder()
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .build()
//                .also {
//                    it.setAnalyzer(cameraExecutor, QrCodeAnalyzer { qrCode ->
//                        if (!qrCodeScannedSuccessfully) {
//                            runOnUiThread {
//                                Toast.makeText(this, "QR 코드 인식 성공!", Toast.LENGTH_SHORT).show()
////                                qrCodeData = qrCode
//                                qrCodeData = "https://assu.com/verify?sessionId=7&adminId=2"
//                                Log.d("QR 인식  성공!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", "성공햇다네요? $qrCode")
//                                binding.tvQrInstruction.text = "QR 코드를 성공적으로 인식했습니다."
//                                setConfirmButtonState(true) // '확인' 버튼 활성화
//                                qrCodeScannedSuccessfully = true // 플래그 설정
//                                cameraProvider.unbindAll()
//                            }
//                        }
//
//                    }
//                    )
//
//
//                }
//
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//            try {
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(
//                    this, cameraSelector, preview, imageAnalyzer
//                )
//            } catch (e: Exception) {
//                Log.e("CameraX", "카메라 바인딩 실패", e)
//            }
//        }, ContextCompat.getMainExecutor(this))
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            startCamera()  TODO 나중에 주석 해제
        } else {
            Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
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
                handleStoreOwnerFlow(idValue as Long)
            }
            "sessionIdAndAdminId" -> {
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
        val fragment = UserTableNumberSelectFragment().apply {
            arguments = Bundle().apply {
                putLong("storeId", storeId)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .addToBackStack(null)
            .commit()
    }

    // 인증 요청자 플로우: 수정 필요
    private fun handleCertificationRequesterFlow(sessionId: Long, adminId: Long) {
        // 로딩 상태 표시
        showCertificationLoadingState()

        // 토큰 확인
        val authToken = getAuthToken()
        Log.d("authToken🫵", authToken)
        if (authToken.isEmpty()) {
            showAuthTokenError()
            return
        }

        // TODO : WebSocket 연결 및 인증 요청 - 임시 주석 처리
        certifyViewModel.subscribeToProgress(sessionId, authToken) // TODO 이거는 인증자 과정에서 필요없는데 테스트 용임
        certifyViewModel.connectAndCertify(sessionId, adminId, authToken)
        // ViewModel 상태 관찰 시작
        observeCertificationStates(9)
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

        // 성공 메시지 표시
        Toast.makeText(this, "그룹 인증이 완료되었습니다!", Toast.LENGTH_SHORT).show()

        // 완료 화면으로 이동
        val fragment = UserPartnershipVerifyCompleteFragment().apply {
            arguments = Bundle().apply {
                putLong("sessionId", sessionId)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .addToBackStack(null)
            .commit()
    }

    // UI 상태 관리 함수들
    private fun showCertificationLoadingState() {
        binding.tvQrInstruction.text = "그룹 인증을 시작합니다..."
        setConfirmButtonState(false)
        // ProgressBar가 있다면 표시
        // binding.progressBar.visibility = View.VISIBLE
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
            Toast.makeText(this, "QR 데이터를 다시 읽어주세요.", Toast.LENGTH_SHORT).show()
            finish()
            startActivity(intent) // 액티비티 재시작
        }
    }

    private fun showAuthTokenError() {
        Toast.makeText(this, "로그인이 필요합니다. 다시 로그인해주세요.", Toast.LENGTH_LONG).show()
        // 로그인 화면으로 이동하는 로직 추가 가능
        finish()
    }

    private fun showCertificationError(error: String) {
        Toast.makeText(this, "인증 오류: $error", Toast.LENGTH_LONG).show()
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
        Toast.makeText(this, "유효하지 않은 QR 코드입니다. 다시 시도해 주세요.", Toast.LENGTH_LONG).show()
        finish()
        startActivity(intent)
    }

    private fun getAuthToken(): String {
//        val sharedPref = getSharedPreferences("auth", Context.MODE_PRIVATE)
//        return sharedPref.getString("token", "") ?: ""
        return "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdXRoUmVhbG0iOiJTU1UiLCJyb2xlIjoiU1RVREVOVCIsInVzZXJJZCI6NiwidXNlcm5hbWUiOiIyMDI0MTY5MyIsImp0aSI6ImI0Y2QyYmRiLWFmNTktNGZkYS05YjUwLThmZjE0OTkzOWMzYSIsImlhdCI6MTc1NzU4ODI0NCwiZXhwIjoxNzU3NTkxODQ0fQ.Xs5tVm-f8WoeQMEYPkta_itLSDOt9pg5awdcRbbH9Ds"
    }

    // Activity 종료 시 WebSocket 연결 해제
    override fun onDestroy() {
        super.onDestroy()
//        cameraExecutor.shutdown() // TODO 나중에 주석 해제

        // WebSocket 연결 해제
        certifyViewModel.disconnect()
    }

    // 이미지 프레임 분석을 위한 클래스
    private class QrCodeAnalyzer(private val onQrCodeScanned: (String) -> Unit) : ImageAnalysis.Analyzer {
        private val reader = MultiFormatReader().apply {
            setHints(mapOf(
                DecodeHintType.POSSIBLE_FORMATS to arrayListOf(BarcodeFormat.QR_CODE)
            ))
        }

        override fun analyze(image: ImageProxy) {
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
                Log.d("QR_SCANNER", "QR 코드 인식 실패", e)
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