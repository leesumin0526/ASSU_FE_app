package com.example.assu_fe_app.ui.partnership

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.domain.model.admin.GetProposalPartnerListModel
import com.example.assu_fe_app.domain.usecase.partnership.GetProposalPartnerListUseCase
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.onError
import com.example.assu_fe_app.util.onFail
import com.example.assu_fe_app.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PartnershipViewModel @Inject constructor(
    private val getProposalPartnerListUseCase: GetProposalPartnerListUseCase
) : ViewModel() {

    sealed interface PartnershipUiState {
        object Idle : PartnershipUiState
        object Loading : PartnershipUiState
        data class Success(val data: List<GetProposalPartnerListModel>) : PartnershipUiState
        data class Fail(val code: Int, val message: String?) : PartnershipUiState
        data class Error(val message: String) : PartnershipUiState
    }

    private val _uiState = MutableStateFlow<PartnershipUiState>(PartnershipUiState.Idle)
    val uiState: StateFlow<PartnershipUiState> = _uiState

    fun getProposalPartnerList(isAll: Boolean) {
        viewModelScope.launch {
            _uiState.value = PartnershipUiState.Loading
            getProposalPartnerListUseCase(isAll)
                .onSuccess { data ->
                    _uiState.value = PartnershipUiState.Success(data)
                }
                .onFail { code ->
                    _uiState.value = PartnershipUiState.Fail(code, "서버 처리 실패")
                }
                .onError { e ->
                    _uiState.value = PartnershipUiState.Error(e.message ?: "네트워크 연결을 확인해주세요.")
                }
        }
    }
}