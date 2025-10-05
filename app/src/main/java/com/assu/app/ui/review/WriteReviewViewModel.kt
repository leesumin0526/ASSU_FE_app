package com.assu.app.ui.review

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assu.app.data.dto.review.request.ReviewWriteRequestDto
import com.assu.app.data.dto.review.response.ReviewWriteResponseDto
import com.assu.app.domain.usecase.review.WriteReviewUseCase
import com.assu.app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class WriteReviewViewModel @Inject constructor(
    private val writeReviewUseCase: WriteReviewUseCase
) : ViewModel(){
    private val _writeResult = MutableLiveData<RetrofitResult<ReviewWriteResponseDto>>()
    val writeResult: LiveData<RetrofitResult<ReviewWriteResponseDto>> = _writeResult

    fun writeReview(
        request: ReviewWriteRequestDto,
        images: List<MultipartBody.Part>
    ) {
        viewModelScope.launch {
            _writeResult.value = writeReviewUseCase(request, images)
        }
    }
    private val _selectedImageUris = MutableLiveData<MutableList<Uri>>(mutableListOf())
    // 외부(Activity)에서는 LiveData로 관찰만 가능하도록 노출
    val selectedImageUris: LiveData<MutableList<Uri>> = _selectedImageUris

    private val _currentImagePickerIndex = MutableLiveData<Int>()
    val currentImagePickerIndex: LiveData<Int> = _currentImagePickerIndex

    // 최대 선택 가능 사진 개수 (상수로 관리 가능)
    companion object {
        const val MAX_PHOTO_COUNT = 3
    }

    fun setCurrentImagePickerIndex(index: Int) {
        // 이미지를 선택/변경하려는 슬롯의 인덱스가 유효한 범위 내에 있는지,
        // 그리고 이전 이미지가 선택되었는지 등의 로직을 추가할 수 있습니다.
        // 예를 들어, 0번 인덱스에 사진이 없는데 1번 인덱스를 선택하려고 할 때를 방지.
        val currentList = _selectedImageUris.value ?: mutableListOf()
        if (index > currentList.size && currentList.size < MAX_PHOTO_COUNT) {
            // 이전 사진부터 순서대로 추가하도록 유도 (UI에서 Toast 등으로 알림)
            // ViewModel에서는 상태 변경을 하지 않거나, 오류 상태를 LiveData로 노출할 수 있음
            return // 또는 특정 상태 LiveData 업데이트
        }
        _currentImagePickerIndex.value = index
    }

    fun addOrUpdateImage(uri: Uri) {
        val currentList = _selectedImageUris.value ?: mutableListOf()
        val currentIndex = _currentImagePickerIndex.value ?: 0 // 현재 선택된 인덱스

        if (currentIndex < currentList.size) {
            // 기존 이미지 변경
            currentList[currentIndex] = uri
        } else if (currentList.size < MAX_PHOTO_COUNT) {
            // 새 이미지 추가 (currentIndex가 currentList.size와 같아야 함)
            if (currentIndex == currentList.size) {
                currentList.add(uri)
            } else {
                // 이 경우는 비정상적인 상태이므로, 로깅하거나 무시할 수 있습니다.
                // 혹은 currentImagePickerIndex 설정 시점에 방지합니다.
                Log.w("ViewModel", "Attempting to add image at an unexpected index in ViewModel.")
                return
            }
        } else {
            // 이미지가 꽉 찼음 (UI에서 Toast 등으로 알림)
            return // 또는 특정 상태 LiveData 업데이트
        }
        _selectedImageUris.value = currentList // LiveData 업데이트하여 UI 갱신 유도
    }

    fun removeImage(index: Int) {
        val currentList = _selectedImageUris.value ?: mutableListOf()
        if (index < currentList.size) {
            currentList.removeAt(index)
            _selectedImageUris.value = currentList
        }
    }

    // 리뷰 텍스트 (필요하다면 ViewModel에서 관리)
    private val _reviewText = MutableLiveData<String>("")
    val reviewText: LiveData<String> = _reviewText

    fun setReviewText(text: String) {
        _reviewText.value = text
    }

    // 작성 완료 버튼 활성화 상태 (LiveData로 관리)
    val isSubmitButtonEnabled: LiveData<Boolean> = androidx.lifecycle.MediatorLiveData<Boolean>().apply {
//        addSource(selectedImageUris) { uris ->
//            value = uris.isNotEmpty() && (reviewText.value?.isNotBlank() == true)
//        }
        addSource(reviewText) { text ->
//            value = (selectedImageUris.value?.isNotEmpty() == true) && text.isNotBlank()
            value = text.isNotBlank()
        }
    }

}