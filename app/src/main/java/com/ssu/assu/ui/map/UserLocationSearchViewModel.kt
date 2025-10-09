package com.ssu.assu.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssu.assu.data.dto.location.LocationUserSearchResultItem
import com.ssu.assu.domain.usecase.dashboard.GetTodayBestStoreUseCase
import com.ssu.assu.domain.usecase.location.UserSearchStoreByKeywordUseCase
import com.ssu.assu.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserLocationSearchViewModel @Inject constructor(
    private val searchUseCase: UserSearchStoreByKeywordUseCase,
    private val bestUseCase : GetTodayBestStoreUseCase
) : ViewModel(){

    private val _bestStores = MutableLiveData<List<String>>()
    val bestStores : MutableLiveData<List<String>> get() = _bestStores
    private val _isEmptyList = MutableLiveData<Boolean>()
    val isEmptyList: LiveData<Boolean> = _isEmptyList

    private val _state = MutableLiveData<String>()
    val state : MutableLiveData<String> get() = _state


    private val _storeList = MutableLiveData<List<LocationUserSearchResultItem>>()
    val storeList: LiveData<List<LocationUserSearchResultItem>> = _storeList



    fun getStoreListByKeyword(keyword: String){
        Log.d("UserLocationSearchViewModel", "getStoreListByKeyword 가 호출됨.")
        viewModelScope.launch {
            _state.value = "loading"
            when (val result = searchUseCase(keyword)) {
                is RetrofitResult.Success -> {
                    _state.value = "success"
                    _storeList.value = result.data
                    if(result.data.isEmpty()){
                        _isEmptyList.value = true
                    }else{
                        _isEmptyList.value = false
                    }
                    Log.d("UserLocationSearchViewModel", "검색 결과: ${result.data}")
                }
                is RetrofitResult.Error -> {
                    Log.e("UserLocationSearchViewModel", "Error: ${result.exception}")
                }
                is RetrofitResult.Fail -> {
                    Log.e("UserLocationSearchViewModel", "Fail: ${result.message}")
                }
            }

        }
    }

    fun getPopularSearch(){
        viewModelScope.launch {

            when(val result = bestUseCase()){
                is RetrofitResult.Success -> {
                    _bestStores.value = result.data.bestStores
                }
                is RetrofitResult.Error -> {}
                is RetrofitResult.Fail -> {}
            }
        }
    }
}