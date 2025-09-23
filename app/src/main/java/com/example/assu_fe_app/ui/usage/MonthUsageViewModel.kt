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

    // 월/연도를 LiveData로 관리하여 두 Fragment에서 공유
    private val _selectedYear = MutableLiveData<Int>(2025)
    val selectedYear: LiveData<Int> = _selectedYear

    private val _selectedMonth = MutableLiveData<Int>(9)
    val selectedMonth: LiveData<Int> = _selectedMonth

    private val _recordList = MutableLiveData<List<ServiceRecord>>()
    val recordList: LiveData<List<ServiceRecord>> = _recordList

    private val _reviewCount = MutableLiveData<Long>()
    val reviewCount: LiveData<Long> = _reviewCount

    // 월/연도 업데이트 함수
    fun updateSelectedDate(year: Int, month: Int) {
        _selectedYear.value = year
        _selectedMonth.value = month
    }

    // 현재 선택된 월/연도 getter
    fun getCurrentYear(): Int = _selectedYear.value ?: 2025
    fun getCurrentMonth(): Int = _selectedMonth.value ?: 9

    fun getMonthUsage(){
        val currentYear = getCurrentYear()
        val currentMonth = getCurrentMonth()

        viewModelScope.launch {
            when(val result = usageUseCase(currentYear, currentMonth)){
                is RetrofitResult.Success -> {
                    _recordList.value = result.data.records
                    _reviewCount.value = result.data.serviceCount

                    Log.d("✅", "Year: $currentYear, Month: $currentMonth, Count: ${_reviewCount.value}")
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