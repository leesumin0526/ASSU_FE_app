package com.example.assu_fe_app.ui.usage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.data.dto.usage.ServiceRecord
import com.example.assu_fe_app.data.dto.usage.response.UserMonthUsageResponseDto
import com.example.assu_fe_app.data.repository.usage.UsageRepository
import com.example.assu_fe_app.domain.model.usage.MonthUsageModel
import com.example.assu_fe_app.domain.usecase.usage.GetUserMonthUsageUseCase
import com.example.assu_fe_app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonthUsageViewModel @Inject constructor(
    private val usageUseCase: GetUserMonthUsageUseCase
) : ViewModel() {

    var year : Int= 2025
    var month = 9


    private val _recordList = MutableLiveData<List<ServiceRecord>>()
    val recordList: LiveData<List<ServiceRecord>> = _recordList

    private val _reviewCount = MutableLiveData<Long>()
    val reviewCount: LiveData<Long> = _reviewCount

    fun getMonthUsage(){
        viewModelScope.launch {
            when(val result = usageUseCase(year, month)){
                is RetrofitResult.Success -> {
                    _recordList.value = result.data.records
                    _reviewCount.value = result.data.serviceCount

                    Log.d("✅", "${_reviewCount}")
                }
                is RetrofitResult.Error -> {
                    Log.d("❌", "RetrofitResult.Fail: The API call ERROR with message: ${result.exception.message}")

                }
                is RetrofitResult.Fail -> {
                    Log.d("❌", "RetrofitResult.Fail: The API call FAIL with message: ${result.message}")
                }

            }
        }
    }

}