package com.ssu.assu.ui.review

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssu.assu.domain.usecase.review.GetMyReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.ssu.assu.data.dto.review.Review
import com.ssu.assu.domain.usecase.review.DeleteReviewUseCase
import com.ssu.assu.util.RetrofitResult
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GetReviewViewModel @Inject constructor(
    private val getReviewUseCase: GetMyReviewUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase
): ViewModel(){

    private val _reviewList = MutableLiveData<List<Review>>()
    val reviewList: LiveData<List<Review>> = _reviewList

    // 삭제 결과를 위한 LiveData 추가
    private val _deleteResult = MutableLiveData<Boolean>()
    val deleteResult: LiveData<Boolean> = _deleteResult

    // 무한 스크롤을 위한 상태 변수
    private var currentPage = 1
    var isFetchingReviews = false
    var isLastPage = false

    var sort: String = "createdAt,desc"

    init {
        // ViewModel이 생성될 때 초기 데이터 로드
        getReviews()
    }

    // 외부에서 호출할 함수 (페이지 번호를 직접 넘기지 않음)
    fun getReviews(){
        Log.d("viewModel✨", "getReviews called")
        // 이미 로딩 중이거나 마지막 페이지라면 중복 호출 방지
        if (isFetchingReviews || isLastPage) {
            Log.d("viewModel✨", "getReviews isFetchingReviews or isLastPage")
            return
        }

        isFetchingReviews = true
        viewModelScope.launch {
            Log.d("viewModel✨", "getReviews called and launched")
            // UseCase를 통해 API 호출
            when (val result = getReviewUseCase(currentPage, 10, sort)) {
                is RetrofitResult.Success -> {
                    Log.d("viewModel✨", "RetrofitResult.Success received")
                    val pageReviewList = result.data
                    Log.d("viewModel✨", "pageReviewList: $pageReviewList")
                    val newReviews = pageReviewList.reviews

                    Log.d("viewModel✨", "Success - newReviews size: ${newReviews.size}")
                    Log.d("viewModel✨", "Success - newReviews content: $newReviews")
                    Log.d("viewModel✨", "Success - isLastPage: ${result.data.isLastPage}")

                    // 기존 목록에 새 데이터를 추가
                    val currentList = _reviewList.value ?: emptyList()
                    Log.d("viewModel✨", "currentList size: ${currentList.size}")
                    val updatedList = currentList + newReviews

                    Log.d("viewModel✨", "Updated list size: ${updatedList.size}")
                    _reviewList.value = updatedList

                    isLastPage = result.data.isLastPage
                    if (!isLastPage) {
                        currentPage++
                    }
                }
                is RetrofitResult.Error -> {
                    Log.d("❌", "RetrofitResult.Error: The API call ERROR with message: ${result.exception.message}")
                }
                is RetrofitResult.Fail -> {
                    Log.d("❌", "RetrofitResult.Fail: The API call failed with message: ${result.message}")
                }
            }
            isFetchingReviews = false
        }
    }

    fun updateSort(newSort: String) {
        if (sort != newSort) {
            sort = newSort
            // 정렬 변경 시 리스트 초기화 & 첫 페이지 다시 불러오기
            resetPagination()
            getReviews()
        }
    }

    // 페이지네이션 상태 초기화 메서드
    private fun resetPagination() {
        currentPage = 1
        isLastPage = false
        _reviewList.value = emptyList()
    }

    fun deleteReview(reviewId: Long) {
        Log.d("viewModel✨", "deleteReview called for reviewId: $reviewId")
        viewModelScope.launch {
            when (val result = deleteReviewUseCase(reviewId)) {
                is RetrofitResult.Success -> {
                    Log.d("viewModel✨", "Delete success for reviewId: $reviewId")

                    // 로컬 리스트에서도 해당 리뷰 제거
                    val currentList = _reviewList.value?.toMutableList() ?: mutableListOf()
                    val updatedList = currentList.filter { it.id != reviewId }
                    _reviewList.value = updatedList

                    _deleteResult.value = true
                }
                is RetrofitResult.Error -> {
                    Log.e("viewModel✨", "Delete error: ${result.exception.message}")
                    _deleteResult.value = false
                }
                is RetrofitResult.Fail -> {
                    Log.e("viewModel✨", "Delete fail: ${result.message}")
                    _deleteResult.value = false
                }
            }
        }
    }
}