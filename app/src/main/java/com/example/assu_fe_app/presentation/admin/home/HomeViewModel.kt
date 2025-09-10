package com.example.assu_fe_app.presentation.admin.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.domain.usecase.notification.GetUnreadExistsUseCase
import com.example.assu_fe_app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUnreadExists: GetUnreadExistsUseCase
) : ViewModel() {

    private val _bellFilled = MutableStateFlow(false)
    val bellFilled: StateFlow<Boolean> = _bellFilled

    fun refreshBell() = viewModelScope.launch {
        when (val res = getUnreadExists()) {
            is RetrofitResult.Success -> _bellFilled.value = res.data
            is RetrofitResult.Fail, is RetrofitResult.Error -> {

            }
        }
    }
}