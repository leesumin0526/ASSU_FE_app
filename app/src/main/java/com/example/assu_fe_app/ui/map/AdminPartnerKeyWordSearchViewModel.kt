package com.example.assu_fe_app.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.data.dto.location.LocationAdminPartnerSearchResultItem
import com.example.assu_fe_app.domain.usecase.location.AdminSearchPartnerByKeywordUseCase
import com.example.assu_fe_app.domain.usecase.location.PartnerSearchAdminByKeywordUseCase
import com.example.assu_fe_app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AdminPartnerKeyWordSearchViewModel @Inject
constructor(
    private val adminSearchPartnerCase: AdminSearchPartnerByKeywordUseCase,
    private val partnerSearchAdminCase: PartnerSearchAdminByKeywordUseCase
) : ViewModel() {

    private val _isEmptyList = MutableLiveData<Boolean>()
    val isEmptyList: LiveData<Boolean> = _isEmptyList

    private val _contentList = MutableLiveData<List<LocationAdminPartnerSearchResultItem>>()
    val contentList: LiveData<List<LocationAdminPartnerSearchResultItem>> = _contentList

    var userRole : String = "PARTNER"

    init{
        // 여기서 로그인된 사용자의 유저 정보를 불러서 업데이트
    }

    fun searchPartners(keyword: String){
        viewModelScope.launch {
            when (val result = adminSearchPartnerCase(keyword)) {
                is RetrofitResult.Success -> {
                    _contentList.value = result.data
                    _isEmptyList.value = result.data.isEmpty()
                    Log.d("admin-partner-search", "${contentList.value}")
                }
                is RetrofitResult.Error -> {
                    Log.d("❌", "Error : ${result.exception.message}")
                }
                is RetrofitResult.Fail -> {
                    Log.d("❌", "Fail : ${result.message}")
                }
            }
        }
    }

    fun searchAdmins(keyword: String){
        viewModelScope.launch {
            Log.d("AdminPartnerKeyWordSearchViewModel", "Partner 기준 Admin 찾기 함수 호출 중")
            when (val result = partnerSearchAdminCase(keyword)) {
                is RetrofitResult.Success -> {
                    _contentList.value = result.data
                    _isEmptyList.value = result.data.isEmpty()
                    Log.d("admin-partner-search", "${contentList.value}")
                }
                is RetrofitResult.Error -> {
                    Log.d("❌", "Error : ${result.exception.message}")
                }
                is RetrofitResult.Fail -> {
                    Log.d("❌", "Fail : ${result.message}")
                }
            }
        }
    }

}