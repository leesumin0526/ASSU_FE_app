package com.example.assu_fe_app.ui.partnership

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.data.dto.partnership.BenefitItem
import com.example.assu_fe_app.data.dto.partnership.CriterionType
import com.example.assu_fe_app.data.dto.partnership.OptionType
import com.example.assu_fe_app.data.dto.partnership.request.CreateDraftRequestDto
import com.example.assu_fe_app.data.dto.partnership.request.PartnershipGoodsRequestDto
import com.example.assu_fe_app.data.dto.partnership.request.PartnershipOptionRequestDto
import com.example.assu_fe_app.data.dto.partnership.request.WritePartnershipRequestDto
import com.example.assu_fe_app.domain.model.partnership.WritePartnershipResponseModel
import com.example.assu_fe_app.domain.model.suggestion.WriteSuggestionModel
import com.example.assu_fe_app.domain.usecase.partnership.CreateDraftPartnershipUseCase
import com.example.assu_fe_app.domain.usecase.partnership.UpdatePartnershipUseCase
import com.example.assu_fe_app.ui.suggestion.SuggestionViewModel.WriteSuggestionUiState
import com.example.assu_fe_app.domain.model.admin.GetProposalAdminListModel
import com.example.assu_fe_app.domain.model.admin.GetProposalPartnerListModel
import com.example.assu_fe_app.domain.usecase.partnership.GetProposalAdminListUseCase
import com.example.assu_fe_app.domain.usecase.partnership.GetProposalPartnerListUseCase
import com.example.assu_fe_app.util.onError
import com.example.assu_fe_app.util.onFail
import com.example.assu_fe_app.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.map
import kotlin.collections.toMutableList

@HiltViewModel
class PartnershipViewModel @Inject constructor(
    private val updatePartnershipUseCase: UpdatePartnershipUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val getProposalPartnerListUseCase: GetProposalPartnerListUseCase,
    private val getProposalAdminListUseCase: GetProposalAdminListUseCase
) : ViewModel() {

    val partnershipStartDate = MutableStateFlow("")
    val partnershipEndDate = MutableStateFlow("")
    val partnerName = MutableStateFlow("")
    val adminName = MutableStateFlow("")

    private var paperId: Long = -1L
    private var partnerId: Long = -1L

    private val _benefitItems = MutableStateFlow<List<BenefitItem>>(emptyList())
    val benefitItems: StateFlow<List<BenefitItem>> = _benefitItems.asStateFlow()

    val isNextButtonEnabled: StateFlow<Boolean> = combine(
        partnerName, adminName, benefitItems
    ) { partner, admin, benefits ->
        partner.isNotBlank() && admin.isNotBlank() && benefits.all { it.criterionValue.isNotBlank() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    private val _writePartnershipState = MutableStateFlow<WritePartnershipUiState>(WritePartnershipUiState.Idle)
    val writePartnershipState: StateFlow<WritePartnershipUiState> = _writePartnershipState.asStateFlow()

    fun initProposalData(partnerId: Long, paperId: Long) {
        this.partnerId = partnerId
        this.paperId = paperId
        if (_benefitItems.value.isEmpty()) {
            _benefitItems.value = listOf(BenefitItem())
        }
    }

    fun addBenefitItem() {
        _benefitItems.value = _benefitItems.value + BenefitItem()
    }

    fun onBenefitEvent(itemIndex: Int, event: BenefitItemEvent) {
        val currentList = _benefitItems.value.toMutableList()
        if (itemIndex !in currentList.indices) return

        val currentItem = currentList[itemIndex]
        val newItem = when (event) {
            is BenefitItemEvent.OptionTypeChanged -> {
                currentItem.copy(
                    optionType = event.newType,
                    discountRate = if (event.newType == OptionType.SERVICE) null else currentItem.discountRate,
                    goods = if (event.newType == OptionType.DISCOUNT) emptyList() else currentItem.goods
                )
            }

            is BenefitItemEvent.CriterionTypeChanged -> {
                currentItem.copy(criterionType = event.newType, criterionValue = "")
            }

            is BenefitItemEvent.CriterionValueChanged -> {
                currentItem.copy(criterionValue = event.value)
            }

            is BenefitItemEvent.GoodAdded -> {
                currentItem.copy(goods = currentItem.goods + "")
            }

            else -> currentItem
        }
        currentList[itemIndex] = newItem
        _benefitItems.value = currentList
    }

    fun resetWritePartnershipState() {
        _writePartnershipState.value = WritePartnershipUiState.Idle
    }
//
    fun onNextButtonClicked() {
        if (paperId == -1L) {
            _writePartnershipState.value = WritePartnershipUiState.Error("잘못된 제안서 정보입니다.")
            return
        }
        viewModelScope.launch {
            _writePartnershipState.value = WritePartnershipUiState.Loading

            val optionsDto = _benefitItems.value.map { benefit ->
                PartnershipOptionRequestDto(
                    optionType = benefit.optionType.name,
                    criterionType = benefit.criterionType.name,
                    people = if (benefit.criterionType == CriterionType.HEADCOUNT) benefit.criterionValue.toIntOrNull() else null,
                    cost = if (benefit.criterionType == CriterionType.PRICE) benefit.criterionValue.toLongOrNull() else null,
                    category = benefit.category,
                    discountRate = benefit.discountRate,
                    goods = benefit.goods.filter { it.isNotBlank() }.map { PartnershipGoodsRequestDto(it) }
                )
            }

            val updateRequest = WritePartnershipRequestDto(
                paperId = paperId, // ✅ savedStateHandle로 받은 paperId 사용
                partnershipPeriodStart = partnershipStartDate.value,
                partnershipPeriodEnd = partnershipEndDate.value,
                options = optionsDto
            )

            updatePartnershipUseCase(updateRequest)
                .onSuccess { finalResponse ->
                    _writePartnershipState.value = WritePartnershipUiState.Success(finalResponse)
                }
                .onFail { code ->
                    _writePartnershipState.value = WritePartnershipUiState.Fail(code, "제안서 업데이트에 실패했습니다.")
                }
                .onError { e ->
                    _writePartnershipState.value = WritePartnershipUiState.Error(e.message ?: "Unknown Error")
                }
        }
    }

    // 제휴 목록 조회
    sealed interface PartnershipPartnerListUiState {
        object Idle : PartnershipPartnerListUiState
        object Loading : PartnershipPartnerListUiState
        data class Success(val data: List<GetProposalPartnerListModel>) : PartnershipPartnerListUiState
        data class Fail(val code: Int, val message: String?) : PartnershipPartnerListUiState
        data class Error(val message: String) : PartnershipPartnerListUiState
    }

    private val _getPartnershipPartnerListUiState = MutableStateFlow<PartnershipPartnerListUiState>(PartnershipPartnerListUiState.Idle)
    val getPartnershipPartnerListUiState: StateFlow<PartnershipPartnerListUiState> = _getPartnershipPartnerListUiState

    fun getProposalPartnerList(isAll: Boolean) {
        viewModelScope.launch {
            _getPartnershipPartnerListUiState.value = PartnershipPartnerListUiState.Loading
            getProposalPartnerListUseCase(isAll)
                .onSuccess { data ->
                    _getPartnershipPartnerListUiState.value = PartnershipPartnerListUiState.Success(data)
                }
                .onFail { code ->
                    _getPartnershipPartnerListUiState.value = PartnershipPartnerListUiState.Fail(code, "서버 처리 실패")
                }
                .onError { e ->
                    _getPartnershipPartnerListUiState.value = PartnershipPartnerListUiState.Error(e.message ?: "네트워크 연결을 확인해주세요.")
                }
        }
    }

    sealed interface PartnershipAdminListUiState {
        object Idle : PartnershipAdminListUiState
        object Loading : PartnershipAdminListUiState
        data class Success(val data: List<GetProposalAdminListModel>) : PartnershipAdminListUiState
        data class Fail(val code: Int, val message: String?) : PartnershipAdminListUiState
        data class Error(val message: String) : PartnershipAdminListUiState
    }

    private val _getPartnershipAdminListUiState = MutableStateFlow<PartnershipAdminListUiState>(PartnershipAdminListUiState.Idle)
    val getPartnershipAdminListUiState: StateFlow<PartnershipAdminListUiState> = _getPartnershipAdminListUiState

    fun getProposalAdminList(isAll: Boolean) {
        viewModelScope.launch {
            _getPartnershipAdminListUiState.value = PartnershipAdminListUiState.Loading
            getProposalAdminListUseCase(isAll)
                .onSuccess { data ->
                    _getPartnershipAdminListUiState.value = PartnershipAdminListUiState.Success(data)
                }
                .onFail { code ->
                    _getPartnershipAdminListUiState.value = PartnershipAdminListUiState.Fail(code, "서버 처리 실패")
                }
                .onError { e ->
                    _getPartnershipAdminListUiState.value = PartnershipAdminListUiState.Error(e.message ?: "네트워크 연결을 확인해주세요.")
                }
        }
    }
}