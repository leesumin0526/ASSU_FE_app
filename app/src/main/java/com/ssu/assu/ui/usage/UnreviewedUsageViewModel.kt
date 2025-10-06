package com.ssu.assu.ui.usage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssu.assu.data.dto.usage.ServiceRecord
import com.ssu.assu.domain.usecase.usage.GetUnreviewedUsageUseCase
import com.ssu.assu.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnreviewedUsageViewModel @Inject constructor(
    private val getUnreviewedUsageUseCase: GetUnreviewedUsageUseCase
) : ViewModel() {

    private val _usageList = MutableLiveData<List<ServiceRecord>>() // 🚨 리뷰 목록을 담을 LiveData
    val usageList: LiveData<List<ServiceRecord>> = _usageList

    // 무한 스크롤을 위한 상태 변수
    private var currentPage = 1
    var isFetchingReviews = false
    var isLastPage = false

    private val _sort : String = "createdAt,desc"

    fun getUnreviewedUsage() {
        if (isFetchingReviews || isLastPage) {
            return
        }

        isFetchingReviews = true
        viewModelScope.launch {
            when (val result = getUnreviewedUsageUseCase(currentPage, 10, _sort)) {
                is RetrofitResult.Success -> {
                    val contentList = result.data
                    val newRecords = contentList.records

                    val currentLsit = _usageList.value ?: emptyList()
                    val updateList = currentLsit + newRecords

                    _usageList.value = updateList

                    isLastPage = result.data.isLastPage
                    if (!isLastPage) {
                        currentPage++
                    }

                }
                is RetrofitResult.Error -> {
                    Log.d("❌", "RetrofitResult.Fail: The API call ERROR with message: ${result.exception.message}")
                }
                is RetrofitResult.Fail -> {
                    Log.d("❌", "RetrofitResult.Fail: The API call failed with message: ${result.message}")
                }

            }
            isFetchingReviews = false
        }
    }

}