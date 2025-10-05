package com.assu.app.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assu.app.data.dto.UserRole
import com.assu.app.data.dto.location.ViewportQuery
import com.assu.app.domain.model.location.AdminOnMap
import com.assu.app.domain.model.location.PartnerOnMap
import com.assu.app.domain.usecase.location.GetNearbyAdminsUseCase
import com.assu.app.domain.usecase.location.GetNearbyPartnersUseCase
import com.assu.app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AdminPartnerLocationViewModel @Inject constructor(
    private val getNearbyAdmins: GetNearbyAdminsUseCase,       // PARTNER → ADMIN 조회
    private val getNearbyPartners: GetNearbyPartnersUseCase    // ADMIN   → PARTNER 조회
) : ViewModel() {

    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
        data class AdminSuccess(val items: List<AdminOnMap>) : UiState
        data class PartnerSuccess(val items: List<PartnerOnMap>) : UiState
        data class Fail(val code: String?, val message: String?) : UiState
        data class Error(val t: Throwable) : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state: StateFlow<UiState> = _state

    fun load(role: UserRole, v: ViewportQuery) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            when (role) {
                UserRole.ADMIN -> { // 관리자 화면: 주변 파트너 조회
                    when (val res = getNearbyPartners(v)) {
                        is RetrofitResult.Success -> _state.value = UiState.PartnerSuccess(res.data)
                        is RetrofitResult.Fail    -> _state.value = UiState.Fail(res.statusCode.toString(), res.message)
                        is RetrofitResult.Error   -> _state.value = UiState.Error(res.exception)
                    }
                }
                UserRole.PARTNER -> { // 파트너 화면: 주변 관리자(기관) 조회
                    when (val res = getNearbyAdmins(v)) {
                        is RetrofitResult.Success -> _state.value = UiState.AdminSuccess(res.data)
                        is RetrofitResult.Fail    -> _state.value = UiState.Fail(res.statusCode.toString(), res.message)
                        is RetrofitResult.Error   -> _state.value = UiState.Error(res.exception)
                    }
                }
                UserRole.STUDENT -> {
                    _state.value = UiState.Fail("ROLE_MISMATCH", "STUDENT는 지원하지 않는 화면입니다.")
                }
            }
        }
    }
}