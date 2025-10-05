package com.assu.app.ui.partnership

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assu.app.data.dto.partnership.BenefitItem
import com.assu.app.data.dto.partnership.CriterionType
import com.assu.app.data.dto.partnership.OptionType
import com.assu.app.data.dto.partnership.request.PartnershipGoodsRequestDto
import com.assu.app.data.dto.partnership.request.PartnershipOptionRequestDto
import com.assu.app.data.dto.partnership.request.WritePartnershipRequestDto
import com.assu.app.domain.usecase.partnership.UpdatePartnershipUseCase
import com.assu.app.domain.model.admin.GetProposalAdminListModel
import com.assu.app.domain.model.admin.GetProposalPartnerListModel
import com.assu.app.domain.model.partnership.ProposalPartnerDetailsModel
import com.assu.app.domain.model.partnership.UpdatePartnershipStatusResponseModel
import com.assu.app.domain.usecase.partnership.GetProposalAdminListUseCase
import com.assu.app.domain.usecase.partnership.GetProposalPartnerListUseCase
import com.assu.app.domain.usecase.partnership.UpdatePartnershipStatusUseCase
import com.assu.app.util.onError
import com.assu.app.util.onFail
import com.assu.app.util.onSuccess
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
    private val getProposalPartnerListUseCase: GetProposalPartnerListUseCase,
    private val getProposalAdminListUseCase: GetProposalAdminListUseCase,
    // ▼ ADD: 제휴 상세 조회 유즈케이스
    private val getPartnershipUseCase: com.assu.app.domain.usecase.partnership.GetPartnershipUseCase,
    private val updatePartnershipStatusUseCase: UpdatePartnershipStatusUseCase
) : ViewModel() {

    // ===== 파트너 제안 리스트 상태 =====
    val partnershipStartDate = MutableStateFlow("")
    val partnershipEndDate = MutableStateFlow("")
    val signature = MutableStateFlow("") // (인)
    val partnerName = MutableStateFlow("")
    val adminName = MutableStateFlow("")
    val signDate = MutableStateFlow("")

    var paperId: Long = -1L
    var partnerId: Long = -1L

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

    val isSubmitButtonEnabled: StateFlow<Boolean> = combine(
        partnershipStartDate,
        partnershipEndDate
    ) { start, end->
        start.isNotBlank() && end.isNotBlank()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    private val _writePartnershipState = MutableStateFlow<WritePartnershipUiState>(WritePartnershipUiState.Idle)
    val writePartnershipState: StateFlow<WritePartnershipUiState> = _writePartnershipState.asStateFlow()

    fun updatePartnerName(name: String) {
        partnerName.value = name
        Log.d("PartnershipViewModel", "Partner name updated: $name")
    }

    fun updateAdminName(name: String) {
        adminName.value = name
        Log.d("PartnershipViewModel", "Admin name updated: $name")
    }

    fun initProposalData(partnerId: Long, paperId: Long) {
        this.partnerId = partnerId
        this.paperId = paperId
        if (_benefitItems.value.isEmpty()) {
            _benefitItems.value = listOf(
                BenefitItem(
                    id = System.currentTimeMillis().toString(),
                    optionType = OptionType.SERVICE,
                    criterionType = CriterionType.PRICE,
                    criterionValue = "",
                    category = "",
                    goods = listOf(""),
                    discountRate = ""
                )
            )
        }
    }

    fun addBenefitItem() {
        val newItem = BenefitItem(
            id = System.currentTimeMillis().toString(),
            optionType = OptionType.SERVICE,
            criterionType = CriterionType.PRICE,
            criterionValue = "",
            category = "",
            goods = listOf(""),
            discountRate = ""
        )
        _benefitItems.value = _benefitItems.value + newItem
    }

    fun onBenefitEvent(itemIndex: Int, event: BenefitItemEvent) {
        val currentList = _benefitItems.value.toMutableList()
        if (itemIndex !in currentList.indices) return

        // '아이템 삭제' 이벤트는 리스트 전체를 변경하므로 여기서 바로 처리
        if (event is BenefitItemEvent.ItemRemoved) {
            currentList.removeAt(itemIndex)
            _benefitItems.value = currentList
            return
        }

        val currentItem = currentList[itemIndex]
        val newItem = when (event) {
            is BenefitItemEvent.OptionTypeChanged -> {
                if (event.newType == OptionType.DISCOUNT) {
                    // '할인 혜택'이 선택된 경우
                    currentItem.copy(
                        optionType = OptionType.DISCOUNT,
                        goods = emptyList(), // 제공 항목 리스트는 비움
                        category = "",       // 카테고리도 초기화
                        discountRate = ""
                    )
                } else {
                    // '서비스 제공'이 선택된 경우
                    currentItem.copy(
                        optionType = OptionType.SERVICE,
                        goods = listOf(""), // 제공 항목 리스트를 기본값으로 초기화
                        category = "",
                        discountRate = ""
                    )
                }
            }
            is BenefitItemEvent.CriterionTypeChanged -> currentItem.copy(criterionType = event.newType, criterionValue = "")
            is BenefitItemEvent.CriterionValueChanged -> currentItem.copy(criterionValue = event.value)
            is BenefitItemEvent.CategoryChanged -> currentItem.copy(category = event.text)
            is BenefitItemEvent.DiscountRateChanged -> currentItem.copy(discountRate = event.rate)
            is BenefitItemEvent.GoodAdded -> {
                if (currentItem.optionType == OptionType.SERVICE){
                    currentItem.copy(goods = currentItem.goods + "")
                } else {
                    currentItem
                }
            }
            is BenefitItemEvent.GoodRemoved -> {
                val updatedGoods = currentItem.goods.toMutableList()
                if (event.goodIndex in updatedGoods.indices) {
                    updatedGoods.removeAt(event.goodIndex)
                }
                // ✅ 서비스 제공에서는 최소 1개 유지
                val finalGoods = if (updatedGoods.isEmpty() && currentItem.optionType == OptionType.SERVICE) {
                    listOf("")
                } else {
                    updatedGoods
                }
                currentItem.copy(goods = finalGoods)
            }
            is BenefitItemEvent.GoodUpdated -> {
                val updatedGoods = currentItem.goods.toMutableList()
                if (event.goodIndex in updatedGoods.indices) {
                    updatedGoods[event.goodIndex] = event.text
                }
                currentItem.copy(goods = updatedGoods)
            }
            is BenefitItemEvent.ItemRemoved -> currentItem // 위에서 이미 처리했으므로 여기서는 변경 없음
        }

        currentList[itemIndex] = newItem
        _benefitItems.value = currentList
    }

    fun resetWritePartnershipState() {
        _writePartnershipState.value = WritePartnershipUiState.Idle
    }

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
                    discountRate = benefit.discountRate.toLongOrNull(),
                    goods = benefit.goods.filter { it.isNotBlank() }.map { PartnershipGoodsRequestDto(it) }
                )
            }

            val updateRequest = WritePartnershipRequestDto(
                paperId = paperId, // ✅ savedStateHandle로 받은 paperId 사용
                partnershipPeriodStart = partnershipStartDate.value.replace(" ", ""),
                partnershipPeriodEnd = partnershipEndDate.value.replace(" ", ""),
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

    // 제휴 상세 조회
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

    private val _partnershipDetailLiveData = MutableLiveData<PartnershipDetailUiState>()
    val partnershipDetailLiveData: MutableLiveData<PartnershipDetailUiState> get() = _partnershipDetailLiveData

    private val _summaryText = MutableStateFlow("")
    val summaryText: StateFlow<String> = _summaryText.asStateFlow()

    fun getPartnershipDetail(partnershipId: Long) {
        Log.d("PartnershipViewModel", "getPartnershipDetail called with id: $partnershipId")
        viewModelScope.launch {
            Log.d("PartnershipViewModel", "Setting Loading state")
            _getPartnershipDetailUiState.value = PartnershipDetailUiState.Loading
            _partnershipDetailLiveData.value = PartnershipDetailUiState.Loading
            getPartnershipUseCase(partnershipId)
                .onSuccess { data ->
                    processPartnershipDetailData(data)
                    _getPartnershipDetailUiState.value =
                        PartnershipDetailUiState.Success(data)
                    _partnershipDetailLiveData.value = PartnershipDetailUiState.Success(data)
                }
                .onFail { code ->
                    val failState = PartnershipDetailUiState.Fail(code, "서버 처리 실패")
                    _getPartnershipDetailUiState.value = failState
                    _partnershipDetailLiveData.value = failState
                }
                .onError { e ->
                    val errorState = PartnershipDetailUiState.Error(e.message ?: "네트워크 연결을 확인해주세요.")
                    _getPartnershipDetailUiState.value = errorState
                    _partnershipDetailLiveData.value = errorState
                }
        }
    }

    private fun processPartnershipDetailData(data: ProposalPartnerDetailsModel) {
        partnershipStartDate.value = data.periodStart
        partnershipEndDate.value = data.periodEnd

        signDate.value = data.updatedAt?.let {
            formatDateToKorean(it.split("T")[0])
        } ?: "-"

        updateSummaryText()

        val benefitItems = data.options.map { option ->
            BenefitItem(
                id = System.currentTimeMillis().toString(),
                optionType = OptionType.valueOf(option.optionType.name),
                criterionType = CriterionType.valueOf(option.criterionType.name),
                criterionValue = when {
                    option.people > 0 -> option.people.toString()
                    option.cost > 0 -> option.cost.toString()
                    else -> ""
                },
                category = option.category,
                goods = option.goods.map { it.goodsName },
                discountRate = if (option.discountRate > 0) option.discountRate.toString() else ""
            )
        }
        updateBenefitItems(benefitItems)

        Log.d("PartnershipViewModel", """
        Partnership Detail Data Processed:
        - partnershipId: ${data.partnershipId}
        - periodStart: ${data.periodStart}
        - periodEnd: ${data.periodEnd}
        - updatedAt: ${data.updatedAt ?: "NULL (using current date)"}
        - signDate: ${signDate.value}
        - adminName: ${adminName.value}
        - partnerName: ${partnerName.value}
         """.trimIndent())
    }

    fun formatDateToKorean(date: String): String {
        return try {
            // "2024-01-15" -> "2024년 01월 15일"
            val parts = date.split("-")
            if (parts.size == 3) {
                "${parts[0]}년 ${parts[1]}월 ${parts[2]}일"
            } else {
                date
            }
        } catch (e: Exception) {
            date
        }
    }

    // ✅ Summary 텍스트 생성도 ViewModel에서
    fun updateSummaryText() {
        val summaryText = buildString {
            append("위와 같이 ")
            append(adminName.value.ifEmpty { "-" })
            append("와의\n제휴를 제안합니다.\n\n")
            append(signDate.value)
            append("\n대표 ")
            append(partnerName.value.ifEmpty { "-" })
        }
        _summaryText.value = summaryText

        Log.d("PartnershipViewModel", "Summary text updated: $summaryText")
    }

    fun updateBenefitItems(items: List<BenefitItem>) {
        _benefitItems.value = items
    }

    // 제휴 상태 업데이트 UiState
    sealed interface UpdatePartnershipStatusUiState {
        object Idle : UpdatePartnershipStatusUiState
        object Loading : UpdatePartnershipStatusUiState
        data class Success(val data: UpdatePartnershipStatusResponseModel) : UpdatePartnershipStatusUiState
        data class Fail(val code: Int, val message: String?) : UpdatePartnershipStatusUiState
        data class Error(val message: String) : UpdatePartnershipStatusUiState
    }

    private val _updatePartnershipStatusUiState =
        MutableStateFlow<UpdatePartnershipStatusUiState>(UpdatePartnershipStatusUiState.Idle)
    val updatePartnershipStatusUiState: StateFlow<UpdatePartnershipStatusUiState> =
        _updatePartnershipStatusUiState

    fun updatePartnershipStatus(partnershipId: Long, status: String) {
        if (_updatePartnershipStatusUiState.value is UpdatePartnershipStatusUiState.Loading) {
            return
        }

        viewModelScope.launch {
            _updatePartnershipStatusUiState.value = UpdatePartnershipStatusUiState.Loading

            updatePartnershipStatusUseCase(partnershipId, status)
                .onSuccess { data ->
                    _updatePartnershipStatusUiState.value = UpdatePartnershipStatusUiState.Success(data)
                }
                .onFail { code ->
                    _updatePartnershipStatusUiState.value =
                        UpdatePartnershipStatusUiState.Fail(code, "상태 변경 실패")
                }
                .onError { e ->
                    _updatePartnershipStatusUiState.value =
                        UpdatePartnershipStatusUiState.Error(e.message ?: "오류 발생")
                }
        }
    }
    // 상태 초기화 함수 추가
    fun resetUpdatePartnershipStatus() {
        _updatePartnershipStatusUiState.value = UpdatePartnershipStatusUiState.Idle
    }
}