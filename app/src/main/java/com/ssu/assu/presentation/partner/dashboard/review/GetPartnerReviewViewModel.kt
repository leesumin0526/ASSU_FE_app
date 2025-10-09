package com.ssu.assu.presentation.partner.dashboard.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssu.assu.data.dto.review.Review
import com.ssu.assu.domain.usecase.review.GetMyStoreAverageUseCase
import com.ssu.assu.domain.usecase.review.GetPartnerReviewUseCase
import com.ssu.assu.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetPartnerReviewViewModel @Inject constructor(
    private val getPartnerReviewUseCase: GetPartnerReviewUseCase,
    private val averageUseCase: GetMyStoreAverageUseCase
) : ViewModel(){
    private val _reviewList = MutableLiveData<List<Review>>()
    val reviewList: LiveData<List<Review>> = _reviewList

    private var currentPage = 1
    var isFetchingReviews = false
    var isLastPage = false


    private val _average = MutableLiveData<Float>()
    val average: LiveData<Float> = _average
    private val sort : String = "createdAt,desc"
    // 다음 페이지 리뷰를 가져오는 함수 (무한 스크롤 리스너가 호출)
    fun getReviews() {
        if (isFetchingReviews || isLastPage) return

        isFetchingReviews = true
        viewModelScope.launch {
            when (val result = getPartnerReviewUseCase(currentPage, 10, sort)) {
                is RetrofitResult.Success -> {
                    val pagedReviewList = result.data
                    val newReviews = pagedReviewList.reviews
                    _reviewList.value = (_reviewList.value ?: emptyList()) + newReviews

                    isLastPage = pagedReviewList.isLastPage
                    if (!isLastPage) {
                        currentPage++
                    }
                }
                is RetrofitResult.Error -> { /* 오류 처리 */ }
                is RetrofitResult.Fail -> { /* 실패 처리 */ }
            }
            isFetchingReviews = false
        }
    }

    fun getAverage(){
        viewModelScope.launch {
            when (val result = averageUseCase()){
                is RetrofitResult.Success -> {
                    _average.value = result.data.score
                }
                is RetrofitResult.Error -> { /* 오류 처리 */ }
                is RetrofitResult.Fail -> { /* 실패 처리 */ }
            }
        }
    }
}