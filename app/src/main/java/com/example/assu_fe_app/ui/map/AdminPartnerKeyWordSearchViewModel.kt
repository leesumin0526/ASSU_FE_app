package com.example.assu_fe_app.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.data.dto.location.LocationAdminPartnerSearchResultItem
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
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
    private val partnerSearchAdminCase: PartnerSearchAdminByKeywordUseCase,
    private val authTokenLocalStore: AuthTokenLocalStore
) : ViewModel() {

    val role = authTokenLocalStore.getUserRoleEnum()
        ?: com.example.assu_fe_app.data.dto.UserRole.ADMIN

    private val _isEmptyList = MutableLiveData<Boolean>()
    val isEmptyList: LiveData<Boolean> = _isEmptyList

    private val _contentList = MutableLiveData<List<LocationAdminPartnerSearchResultItem>>()
    val contentList: LiveData<List<LocationAdminPartnerSearchResultItem>> = _contentList

    fun search(keyword: String) = viewModelScope.launch {
        val result = when (role) {
            com.example.assu_fe_app.data.dto.UserRole.ADMIN   -> adminSearchPartnerCase(keyword)
            com.example.assu_fe_app.data.dto.UserRole.PARTNER -> partnerSearchAdminCase(keyword)
            else                                              -> adminSearchPartnerCase(keyword)
        }
        when (result) {
            is RetrofitResult.Success -> { _contentList.value = result.data; _isEmptyList.value = result.data.isEmpty() }
            is RetrofitResult.Fail, is RetrofitResult.Error -> { _contentList.value = emptyList(); _isEmptyList.value = true }
        }
    }

}