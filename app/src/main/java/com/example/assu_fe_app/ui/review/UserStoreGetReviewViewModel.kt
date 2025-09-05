package com.example.assu_fe_app.ui.review

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.data.dto.review.Review
import com.example.assu_fe_app.domain.usecase.review.GetUserStoreReviewAverageUseCase
import com.example.assu_fe_app.domain.usecase.review.GetUserStoreReviewUseCase
import com.example.assu_fe_app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class UserStoreGetReviewViewModel @Inject constructor(
    private val getStoreReviewUseCase: GetUserStoreReviewUseCase,
    private val averageUseCase: GetUserStoreReviewAverageUseCase
) : ViewModel() {
    private val _reviewList = MutableLiveData<List<Review>>()
    val reviewList: LiveData<List<Review>> = _reviewList

    private var storeId : Long = 0L
    fun initStoreId(id: Long) {
        if (this.storeId == 0L) { // storeId가 아직 설정되지 않았을 때만 초기화
            this.storeId = id
        }
    }

    private var currentPage=1
    var isFetchingReviews = false
    var isLastPage = false
    var sort: String = "createdAt,desc"

    private val _average = MutableLiveData<Float>()
    val average: LiveData<Float> = _average


    fun getReviews(){
        if(isFetchingReviews|| isLastPage) return

        isFetchingReviews = true
        viewModelScope.launch {
            when(val result = getStoreReviewUseCase(storeId, currentPage, 10, sort)){
                is RetrofitResult.Success -> {
                    val pagedReviewList = result.data
                    val newReviews = pagedReviewList.reviews
                    _reviewList.value = (_reviewList.value ?: emptyList()) + newReviews

                    isLastPage = pagedReviewList.isLastPage
                    if(!isLastPage){
                        currentPage++
                    }
                }
                is RetrofitResult.Error -> { /* 오류 처리 */ }
                is RetrofitResult.Fail -> { /* 실패 처리 */ }
            }
            isFetchingReviews = false
        }
    }

    fun updateSort(newSort: String) {
        if (sort != newSort) {
            sort = newSort
            // 정렬 변경 시 리스트 초기화 & 첫 페이지 다시 불러오기
            currentPage = 1
            isLastPage = false
            _reviewList.value = emptyList()
            getReviews()
        }
    }

    fun getAverage(){
        viewModelScope.launch {
            when (val result = averageUseCase(storeId)){
                is RetrofitResult.Success -> {
                    _average.value = result.data.score
                    Log.d("평점", average.toString())
                }
                is RetrofitResult.Error -> { /* 오류 처리 */ }
                is RetrofitResult.Fail -> { /* 실패 처리 */ }
            }
        }
    }



}