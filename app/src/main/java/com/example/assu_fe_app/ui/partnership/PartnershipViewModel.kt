package com.example.assu_fe_app.ui.partnership

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.domain.model.admin.GetProposalAdminListModel
import com.example.assu_fe_app.domain.model.admin.GetProposalPartnerListModel
import com.example.assu_fe_app.domain.model.partnership.ProposalPartnerDetailsModel
import com.example.assu_fe_app.domain.usecase.partnership.GetProposalAdminListUseCase
import com.example.assu_fe_app.domain.usecase.partnership.GetProposalPartnerListUseCase
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
    private val getProposalPartnerListUseCase: GetProposalPartnerListUseCase,
    private val getProposalAdminListUseCase: GetProposalAdminListUseCase,
    // ▼ ADD: 제휴 상세 조회 유즈케이스
    private val getPartnershipUseCase: com.example.assu_fe_app.domain.usecase.partnership.GetPartnershipUseCase
) : ViewModel() {

    // ===== 파트너 제안 리스트 상태 =====
    sealed interface PartnershipPartnerListUiState {
        object Idle : PartnershipPartnerListUiState
        object Loading : PartnershipPartnerListUiState
        data class Success(val data: List<GetProposalPartnerListModel>) : PartnershipPartnerListUiState
        data class Fail(val code: Int, val message: String?) : PartnershipPartnerListUiState
        data class Error(val message: String) : PartnershipPartnerListUiState
    }

    private val _getPartnershipPartnerListUiState =
        MutableStateFlow<PartnershipPartnerListUiState>(PartnershipPartnerListUiState.Idle)
    val getPartnershipPartnerListUiState: StateFlow<PartnershipPartnerListUiState> =
        _getPartnershipPartnerListUiState

    fun getProposalPartnerList(isAll: Boolean) {
        viewModelScope.launch {
            _getPartnershipPartnerListUiState.value = PartnershipPartnerListUiState.Loading
            getProposalPartnerListUseCase(isAll)
                .onSuccess { data ->
                    _getPartnershipPartnerListUiState.value =
                        PartnershipPartnerListUiState.Success(data)
                }
                .onFail { code ->
                    _getPartnershipPartnerListUiState.value =
                        PartnershipPartnerListUiState.Fail(code, "서버 처리 실패")
                }
                .onError { e ->
                    _getPartnershipPartnerListUiState.value =
                        PartnershipPartnerListUiState.Error(e.message ?: "네트워크 연결을 확인해주세요.")
                }
        }
    }

    // ===== 어드민 제안 리스트 상태 =====
    sealed interface PartnershipAdminListUiState {
        object Idle : PartnershipAdminListUiState
        object Loading : PartnershipAdminListUiState
        data class Success(val data: List<GetProposalAdminListModel>) : PartnershipAdminListUiState
        data class Fail(val code: Int, val message: String?) : PartnershipAdminListUiState
        data class Error(val message: String) : PartnershipAdminListUiState
    }

    private val _getPartnershipAdminListUiState =
        MutableStateFlow<PartnershipAdminListUiState>(PartnershipAdminListUiState.Idle)
    val getPartnershipAdminListUiState: StateFlow<PartnershipAdminListUiState> =
        _getPartnershipAdminListUiState

    fun getProposalAdminList(isAll: Boolean) {
        viewModelScope.launch {
            _getPartnershipAdminListUiState.value = PartnershipAdminListUiState.Loading
            getProposalAdminListUseCase(isAll)
                .onSuccess { data ->
                    _getPartnershipAdminListUiState.value =
                        PartnershipAdminListUiState.Success(data)
                }
                .onFail { code ->
                    _getPartnershipAdminListUiState.value =
                        PartnershipAdminListUiState.Fail(code, "서버 처리 실패")
                }
                .onError { e ->
                    _getPartnershipAdminListUiState.value =
                        PartnershipAdminListUiState.Error(e.message ?: "네트워크 연결을 확인해주세요.")
                }
        }
    }

    sealed interface PartnershipDetailUiState {
        object Idle : PartnershipDetailUiState
        object Loading : PartnershipDetailUiState
        data class Success(
            val data: ProposalPartnerDetailsModel
        ) : PartnershipDetailUiState
        data class Fail(val code: Int, val message: String?) : PartnershipDetailUiState
        data class Error(val message: String) : PartnershipDetailUiState
    }

    private val _getPartnershipDetailUiState =
        MutableStateFlow<PartnershipDetailUiState>(PartnershipDetailUiState.Idle)
    val getPartnershipDetailUiState: StateFlow<PartnershipDetailUiState> =
        _getPartnershipDetailUiState

    fun getPartnershipDetail(partnershipId: Long) {
        viewModelScope.launch {
            _getPartnershipDetailUiState.value = PartnershipDetailUiState.Loading
            getPartnershipUseCase(partnershipId)
                .onSuccess { data ->
                    _getPartnershipDetailUiState.value =
                        PartnershipDetailUiState.Success(data)
                }
                .onFail { code ->
                    _getPartnershipDetailUiState.value =
                        PartnershipDetailUiState.Fail(code, "서버 처리 실패")
                }
                .onError { e ->
                    _getPartnershipDetailUiState.value =
                        PartnershipDetailUiState.Error(e.message ?: "네트워크 연결을 확인해주세요.")
                }
        }
    }

}