package com.example.assu_fe_app.presentation.user.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.ActivityUserQrVerifyBinding
import com.example.assu_fe_app.presentation.base.BaseActivity

class UserQRVerifyActivity :
    BaseActivity<ActivityUserQrVerifyBinding>(R.layout.activity_user_qr_verify) {

    override fun initView() {
        applyWindowInsetPadding()

        // 오버레이 위치 설정
        binding.previewView.post {
            binding.overlay.updateHoleRectFromView(binding.qrGuideBox)
        }

        // ← 버튼 클릭 시 finish()
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 확인 버튼 클릭 처리
        binding.btnConfirm.setOnClickListener {
            Toast.makeText(this, "인증이 완료되었습니다.", Toast.LENGTH_SHORT).show()

            // 카메라 및 오버레이 숨기기
            binding.previewView.visibility = View.GONE
            binding.overlay.visibility = View.GONE

            // FragmentContainerView 보이게
            binding.fragmentContainerView.visibility = View.VISIBLE

            // 처음 프래그먼트로 테이블 선택 띄우기
            showTableNumberFragment()
        }

        // 예시로 학교명, 단과대 표시
        binding.tvUniversity.text = "숭실대학교 학생"
        binding.tvDepartment.text = "IT대학"
    }

    private val CAMERA_PERMISSION_CODE = 100

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

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview
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

    fun showTableNumberFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, UserTableNumberSelectFragment())
            .addToBackStack(null)
            .commit()
    }

    fun showPartnershipSelectFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, UserPartnershipSelectFragment())
            .addToBackStack(null)
            .commit()
    }

    fun showVerifyCompleteFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, UserPartnershipVerifyCompleteFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun initObserver() {
        // 추후 QR 인식 결과 LiveData 등 관찰할 경우
    }

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
}
