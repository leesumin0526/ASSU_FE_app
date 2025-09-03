package com.example.assu_fe_app.presentation.user.review.writing

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.review.request.ReviewWriteRequestDto
import com.example.assu_fe_app.databinding.ActivityUserPhotoReviewBinding
import com.example.assu_fe_app.presentation.base.BaseActivity
import com.example.assu_fe_app.ui.review.WriteReviewViewModel
import com.example.assu_fe_app.util.RetrofitResult
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import android.Manifest
import kotlin.io.path.exists

@AndroidEntryPoint
class UserPhotoReviewActivity : BaseActivity<ActivityUserPhotoReviewBinding>(R.layout.activity_user_photo_review) {

    private val reviewViewModel: WriteReviewViewModel by viewModels()
    private lateinit var photoImageViews : List<ImageView>

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    // 선택된 이미지의 URI를 사용하여 ImageView에 표시
                    reviewViewModel.addOrUpdateImage(uri)
                    // 필요하다면 URI를 사용하여 다른 작업 수행 (예: 서버 업로드 준비)
                    Log.d("PhotoReview", "Selected image URI: $uri")
                }
            } else {
                Toast.makeText(this, "사진 선택이 취소되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    // 권한 요청을 위한 ActivityResultLauncher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // 권한이 허용된 경우 갤러리 열기
                openGallery()
            } else {
                // 권한이 거부된 경우 사용자에게 알림
                Toast.makeText(this, "갤러리 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    override fun initView() {
        photoImageViews = listOf(
            binding.ivPhotoReviewPhoto1,
            binding.ivPhotoReviewPhoto2,
            binding.ivPhotoReviewPhoto3
        )

        val rating = intent.getIntExtra("rating", 0)
        Log.d("넘어온 rate", rating.toString())
        val adminName = intent.getStringExtra("adminName")
        val content = intent.getStringExtra("content")
        val partnershipUsageId = intent.getLongExtra("partnershipUsageId", 0)

        binding.tvPhotoReviewAdmin.text = adminName

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val extraPaddingTop = 3
            v.setPadding(
                systemBars.left,
                systemBars.top + extraPaddingTop.dpToPx(v.context),
                systemBars.right,
                0
            )
            insets
        }

        // 각 이미지 뷰 클릭 리스너 설정
        photoImageViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                val currentSelectedCount = reviewViewModel.selectedImageUris.value?.size ?: 0
                // ViewModel에 현재 이미지 피커 인덱스 설정 요청
                // 조건부 갤러리 오픈 로직 (ViewModel과 유사하게)
                if (index > currentSelectedCount && currentSelectedCount < WriteReviewViewModel.MAX_PHOTO_COUNT) {
                    Toast.makeText(this, "이전 사진부터 순서대로 추가해주세요.", Toast.LENGTH_SHORT).show()
                } else if (currentSelectedCount >= WriteReviewViewModel.MAX_PHOTO_COUNT && index >= currentSelectedCount) {
                    Toast.makeText(this, "최대 ${WriteReviewViewModel.MAX_PHOTO_COUNT}개의 사진만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    reviewViewModel.setCurrentImagePickerIndex(index)
                    checkPermissionAndOpenGallery()
                }
            }
            // 이미지 삭제 기능 (예: 길게 누르기)
            imageView.setOnLongClickListener {
                if (index < (reviewViewModel.selectedImageUris.value?.size ?: 0)) {
                    reviewViewModel.removeImage(index)
                    Toast.makeText(this, "사진이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
                true // 이벤트 소비
            }
        }

        val backButton = binding.ivMyPhotoReviewBackArrow
        backButton.setOnClickListener{
            finish() // StarReviewActivity로 돌아감
        }

        val finishWritingDeactivatedButton = binding.layoutFinishReviewDeactivatedButton
        val finishWritingActivatedButton = binding.layoutFinishReviewActivatedButton
        var review = binding.etWritePhotoReview

        review.addTextChangedListener {
            val text = it.toString()
            reviewViewModel.setReviewText(text)
        }

        finishWritingActivatedButton.setOnClickListener {
            val request = ReviewWriteRequestDto(
                rate = rating,
                partnerId =9L, // TODO: 실제 파트너 ID로 교체
                storeId = 2L, // TODO: 실제 스토어 ID로 교체
                content = reviewViewModel.reviewText.value ?: "",
                partnershipUsageId = 3L, // TODO: 실제 partnershipUsageID로 교체
                adminName = "IT대학 학생회" // TODO: 실제 adminName 값으로 넣어주기
            )

            // 파일 URI를 MultipartBody.Part로 변환하여 ViewModel에 전달
            val imagesToUpload = reviewViewModel.selectedImageUris.value?.mapNotNull { uri ->
                uriToMultipartBodyPart(uri)
            } ?: emptyList()

            // 이미지와 리뷰 데이터를 ViewModel을 통해 서버로 전송
            reviewViewModel.writeReview(request, imagesToUpload)
        }

    }

    override fun initObserver() {
        reviewViewModel.selectedImageUris.observe(this) { uris ->
            updatePhotoImageViews(uris)
        }

        reviewViewModel.isSubmitButtonEnabled.observe(this) { isEnabled ->
            binding.layoutFinishReviewActivatedButton.visibility = if (isEnabled) View.VISIBLE else View.INVISIBLE
            binding.layoutFinishReviewDeactivatedButton.visibility = if (isEnabled) View.GONE else View.VISIBLE
        }

        reviewViewModel.writeResult.observe(this) { result ->
            when (result) {
                is RetrofitResult.Success -> {
                    Toast.makeText(this, "리뷰가 성공적으로 작성되었습니다!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, UserFinishReviewActivity::class.java)
                    startActivity(intent)
                    finish() // 현재 액티비티 종료
                }
                is RetrofitResult.Error -> {
//                    Toast.makeText(this, "리뷰 작성 실패 error ${result.exception.message}", Toast.LENGTH_LONG).show()
                    Log.d("리뷰 작성 실패", result.exception.message.toString())
                }
                is RetrofitResult.Fail -> {
                    Toast.makeText(this, "리뷰 작성 실패: ${result.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updatePhotoImageViews(uris: List<Uri>?) {
        val currentUris = uris ?: emptyList()
        photoImageViews.forEachIndexed { index, imageView ->
            if (index < currentUris.size) {
                imageView.visibility = View.VISIBLE
                Glide.with(this)
                    .load(currentUris[index])
//                    .placeholder(R.drawable.ic_image_loading_placeholder) // 로딩 중 표시할 이미지
//                    .error(R.drawable.ic_image_load_error_placeholder) // 에러 시 표시할 이미지
                    .into(imageView)
            } else {
                // 이미지가 없는 슬롯의 UI 처리
                if (index == 0 || (index > 0 && index -1 < currentUris.size)) {
                    imageView.visibility = View.VISIBLE
                    imageView.setImageResource(R.drawable.ic_select_photo) // 기본 '사진 추가' 아이콘
                } else {
                    imageView.visibility = View.INVISIBLE
                }
            }
        }
    }


    private fun uriToMultipartBodyPart(uri: Uri): MultipartBody.Part? {
        return try {
            val file = getFileFromUri(this, uri) ?: return null
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            // 파일 이름은 서버 요구사항에 따라 설정 (여기서는 원본 파일 이름 사용)
            MultipartBody.Part.createFormData("reviewImages", file.name, requestFile)
        } catch (e: Exception) {
            Log.e("UriToMultipart", "Error converting URI to MultipartBody.Part", e)
            null
        }
    }

    // URI로부터 실제 파일 경로를 가져오는 함수 (ContentResolver 사용)
    // 이 함수는 다양한 URI 스킴(content://, file://)을 처리할 수 있도록 개선이 필요할 수 있습니다.
    // 특히, API 29 (Android 10) 이상에서는 Scoped Storage로 인해 직접 파일 경로 접근이 제한될 수 있습니다.
    // getFileFromUri 함수는 좀 더 견고하게 만들어야 합니다. 아래는 간단한 예시입니다.
    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val fileName = getFileName(context, uri) ?: return null
        // 앱의 캐시 디렉토리에 임시 파일 생성
        val tempFile = File(context.cacheDir, fileName)
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            return tempFile
        } catch (e: Exception) {
            Log.e("GetFileFromUri", "Failed to create temp file from URI", e)
            // 임시 파일 생성 실패 시 삭제
            if (tempFile.exists()) {
                tempFile.delete()
            }
            return null
        }
    }

    // URI에서 파일 이름을 가져오는 함수 (ContentResolver 사용)
    private fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    val displayNameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        result = cursor.getString(displayNameIndex)
                    }
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1 && cut != null) {
                result = result.substring(cut + 1)
            }
        }
        // 파일 이름이 너무 길거나 유효하지 않은 문자를 포함하는 경우를 대비한 처리 추가 가능
        return result ?: "temp_image_${System.currentTimeMillis()}" // 기본 파일 이름
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    private fun checkPermissionAndOpenGallery() {
        // Android 버전에 따라 필요한 권한이 다릅니다.
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 이미 권한이 허용된 경우 갤러리 열기
                openGallery()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // 사용자가 이전에 권한 요청을 거부했지만, 다시 거부하지 않은 경우
                // 왜 권한이 필요한지 설명하는 UI를 보여주고 다시 요청할 수 있습니다.
                // (예: 다이얼로그 표시)
                Toast.makeText(this, "사진을 선택하려면 갤러리 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show()
                // 이 예제에서는 바로 다시 요청합니다.
                requestPermissionLauncher.launch(permission)
            }
            else -> {
                // 처음 권한을 요청하거나, 사용자가 "다시 묻지 않음"을 선택한 경우
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK) // ACTION_PICK은 일반적으로 갤러리 앱을 엽니다.
        // val intent = Intent(Intent.ACTION_GET_CONTENT) // 이것도 사용 가능하며, 파일 탐색기 등 더 광범위한 선택기를 제공할 수 있습니다.
        intent.type = "image/*" // 이미지 타입만 선택하도록 필터링
        pickImageLauncher.launch(intent)
    }
}