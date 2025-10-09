package com.ssu.assu.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssu.assu.data.dto.location.LocationAdminPartnerSearchResultItem
import com.ssu.assu.data.local.AuthTokenLocalStore
import com.ssu.assu.domain.usecase.location.AdminSearchPartnerByKeywordUseCase
import com.ssu.assu.domain.usecase.location.PartnerSearchAdminByKeywordUseCase
import com.ssu.assu.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AdminPartnerKeyWordSearchViewModel @Inject
constructor(
    private val adminSearchPartnerCase: AdminSearchPartnerByKeywordUseCase,
    private val partnerSearchAdminCase: PartnerSearchAdminByKeywordUseCase,
    private val authTokenLocalStore: AuthTokenLocalStore
) : ViewModel() {

    private val _state = MutableLiveData<String>()
    val state : LiveData<String> = _state

    val role = authTokenLocalStore.getUserRoleEnum()
        ?: com.ssu.assu.data.dto.UserRole.ADMIN

    private val _isEmptyList = MutableLiveData<Boolean>()
    val isEmptyList: LiveData<Boolean> = _isEmptyList

    private val _contentList = MutableLiveData<List<LocationAdminPartnerSearchResultItem>>()
    val contentList: LiveData<List<LocationAdminPartnerSearchResultItem>> = _contentList

    fun search(keyword: String) = viewModelScope.launch {
        _state.value = "loading"
        val result = when (role) {
            com.ssu.assu.data.dto.UserRole.ADMIN   -> adminSearchPartnerCase(keyword)
            com.ssu.assu.data.dto.UserRole.PARTNER -> partnerSearchAdminCase(keyword)
            else                                              -> adminSearchPartnerCase(keyword)
        }
        when (result) {
            is RetrofitResult.Success -> {
                _contentList.value = result.data
                _isEmptyList.value = result.data.isEmpty()
                _state.value = "success"
            }
            is RetrofitResult.Fail, is RetrofitResult.Error -> {
                _contentList.value = emptyList()
                _isEmptyList.value = true
                _state.value = "error"  // 이 부분이 누락되어 있었음!
            }
        }
    }

}