package com.example.assu_fe_app.ui.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.domain.usecase.location.SearchPlaceUseCase
import com.example.assu_fe_app.presentation.common.search.LocationInfo
import com.example.assu_fe_app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationInfoSearchViewModel @Inject constructor(
    private val searchPlaceUseCase: SearchPlaceUseCase
) : ViewModel() {

    private val _state = MutableLiveData<String>()
    val state: MutableLiveData<String> get() = _state

    private val _locationInfoList = MutableLiveData<List<LocationInfo>>()
    val locationInfoList: MutableLiveData<List<LocationInfo>> get() = _locationInfoList

    fun searchLocationByKakao(keyword: String) {
        viewModelScope.launch {
            _state.value = "loading"
            when (val result = searchPlaceUseCase(keyword, 5)) {
                is RetrofitResult.Success -> {
                    _locationInfoList.value = result.data
                    _state.value = "success"
                }
                is RetrofitResult.Error -> {
                    // 에러 처리
                    Log.d("locationInfoViewModel", "error : ${result.exception}")
                }
                is RetrofitResult.Fail -> {
                    Log.d("locationInfoViewModel", "fail : ${result.message}")
                }
            }
        }
    }

}