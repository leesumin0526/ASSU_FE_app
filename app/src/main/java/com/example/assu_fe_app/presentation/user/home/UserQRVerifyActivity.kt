package com.example.assu_fe_app.presentation.user.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings.Global.putString
import android.util.Log
import android.widget.Toast
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
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UserQRVerifyActivity :
    BaseActivity<ActivityUserQrVerifyBinding>(R.layout.activity_user_qr_verify) {

    private lateinit var cameraExecutor: ExecutorService
    private var qrCodeScannedSuccessfully = false // QR 인식 성공 여부 플래그
    private val CAMERA_PERMISSION_CODE = 100
    private var qrCodeData: String? = null


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
                showTableNumberSelectFragment()
            }
        }

        binding.tvConfirm.setOnClickListener { // 텍스트뷰 클릭 리스너
            if (qrCodeScannedSuccessfully) {
                Toast.makeText(this, "인증이 완료되었습니다.", Toast.LENGTH_SHORT).show()

                // 다음 프래그먼트로 전환
                showTableNumberSelectFragment()
            }
        }

        binding.tvUniversity.text = "숭실대학교 학생"
        binding.tvDepartment.text = "IT대학"

        cameraExecutor = Executors.newSingleThreadExecutor()
        checkCameraPermission()
    }

    override fun initObserver() {
        // 이 액티비티에서는 따로 LiveData를 관찰하지 않으므로 비워둡니다.
    }



    // 이 함수는 Activity에서만 필요한 로직이므로, BaseActivity가 아닌 이 클래스 내부에 두는 것이 좋습니다.
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
                        if (!qrCodeScannedSuccessfully) {
                            runOnUiThread {
                                Toast.makeText(this, "QR 코드 인식 성공!", Toast.LENGTH_SHORT).show()
                                qrCodeData = qrCode
                                Log.d("QR 인식 성공!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", "성공햇다네요? $qrCode")
                                binding.tvQrInstruction.text = "QR 코드를 성공적으로 인식했습니다."
                                setConfirmButtonState(true) // '확인' 버튼 활성화
                                qrCodeScannedSuccessfully = true // 플래그 설정
                                cameraProvider.unbindAll()
                            }
                        }

                    }
                    )


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
            Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
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

    private fun showTableNumberSelectFragment() {

        val storeIdValue: Long? = qrCodeData?.let { data ->
            if (data.contains("storeId=")) {
                data.split("storeId=").lastOrNull()?.toLong()
            } else {
                null
            }
        }

        if (storeIdValue != null) {
            // storeIdValue가 유효한 경우, 다음 프래그먼트로 이동
            val fragment = UserTableNumberSelectFragment().apply {
                arguments = Bundle().apply {
                    putLong("storeId", storeIdValue)
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .addToBackStack(null)
                .commit()
        } else {
            // storeIdValue가 null인 경우, 사용자에게 알리고 카메라 인식 모드로 돌아감
            Toast.makeText(this, "유효하지 않은 QR 코드입니다. 다시 시도해 주세요.", Toast.LENGTH_LONG).show()

            // 현재 액티비티를 종료하고 재시작
            finish()
            startActivity(intent)
        }
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