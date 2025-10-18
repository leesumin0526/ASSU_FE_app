package com.ssu.assu.ui.review

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssu.assu.data.dto.review.Review
import com.ssu.assu.data.dto.review.ReviewStoreItem
import com.ssu.assu.data.dto.store.PaperContent
import com.ssu.assu.domain.usecase.review.GetUserStoreReviewAverageUseCase
import com.ssu.assu.domain.usecase.review.GetUserStoreReviewUseCase
import com.ssu.assu.domain.usecase.store.GetStorePartnershipUseCase
import com.ssu.assu.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserStoreGetReviewViewModel @Inject constructor(
    private val getStoreReviewUseCase: GetUserStoreReviewUseCase,
    private val averageUseCase: GetUserStoreReviewAverageUseCase,
    private val getMyPartnershipUseCase : GetStorePartnershipUseCase
) : ViewModel() {
    private val _reviewList = MutableLiveData<List<Review>>()
    val reviewList: LiveData<List<Review>> = _reviewList

    private val _partnershipContentList = MutableLiveData<List<ReviewStoreItem>>()
    val partnershipContentList: LiveData<List<ReviewStoreItem>> = _partnershipContentList

    var storeName: String = ""
    private var storeId : Long = 0L
    fun initStoreId(id: Long) {
        if (this.storeId == 0L) {
            this.storeId = id
        }
    }

    private var currentPage = 1
    var isFetchingReviews = false
    var isLastPage = false
    var sort: String = "createdAt,desc"

    private val _average = MutableLiveData<Float>()
    val average: LiveData<Float> = _average


    fun getReviews(){
        if(isFetchingReviews || isLastPage) return

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
                is RetrofitResult.Error -> {
                    Log.e("getReviews", "Error: ${result.exception}")
                }
                is RetrofitResult.Fail -> {
                    Log.e("getReviews", "Fail: ${result.message}")
                }
            }
            isFetchingReviews = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getPartnershipForMe(){
        viewModelScope.launch {
            when (val result = getMyPartnershipUseCase(storeId)){
                is RetrofitResult.Success -> {
                    _partnershipContentList.value = toReviewStoreItem(result.data.contents)
                }
                is RetrofitResult.Error -> {
                    Log.d("getPartnershipForMe", "${result.exception}")
                }
                is RetrofitResult.Fail -> {
                    Log.d("getPartnershipForMe","${result.message}" )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun toReviewStoreItem(contents : List<PaperContent>) : List<ReviewStoreItem>{
        return contents.map { content ->
            ReviewStoreItem(content.adminName, content.paperContent)
        }
    }

    fun updateSort(newSort: String) {
        if (sort != newSort) {
            sort = newSort
            // 정렬 변경 시 리스트 초기화 & 첫 페이지 다시 불러오기
            resetPagination()
            getReviews() // Fragment에서 추가 호출하지 않도록 여기서만 호출
        }
    }

    // 페이지네이션 상태 초기화 메서드 추가
    private fun resetPagination() {
        currentPage = 1
        isLastPage = false
        _reviewList.value = emptyList()
    }

    fun getAverage(){
        viewModelScope.launch {
            when (val result = averageUseCase(storeId)){
                is RetrofitResult.Success -> {
                    _average.value = result.data.score
                    Log.d("평점", average.value.toString())
                }
                is RetrofitResult.Error -> {
                    Log.e("getAverage", "Error: ${result.exception}")
                }
                is RetrofitResult.Fail -> {
                    Log.e("getAverage", "Fail: ${result.message}")
                }
            }
        }
    }
}