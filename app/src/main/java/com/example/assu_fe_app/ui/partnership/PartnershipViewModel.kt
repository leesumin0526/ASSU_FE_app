package com.example.assu_fe_app.ui.partnership

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.data.dto.partnership.BenefitItem
import com.example.assu_fe_app.data.dto.partnership.CriterionType
import com.example.assu_fe_app.data.dto.partnership.OptionType
import com.example.assu_fe_app.data.dto.partnership.request.PartnershipGoodsRequestDto
import com.example.assu_fe_app.data.dto.partnership.request.PartnershipOptionRequestDto
import com.example.assu_fe_app.data.dto.partnership.request.WritePartnershipRequestDto
import com.example.assu_fe_app.domain.model.suggestion.WriteSuggestionModel
import com.example.assu_fe_app.domain.usecase.partnership.WritePartnershipUseCase
import com.example.assu_fe_app.ui.suggestion.SuggestionViewModel.WriteSuggestionUiState
import com.example.assu_fe_app.util.onFail
import com.example.assu_fe_app.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.map
import kotlin.collections.toMutableList

@HiltViewModel
class PartnershipViewModel @Inject constructor(
    private val writePartnershipUseCase: WritePartnershipUseCase
) : ViewModel() {

    val partnerName = MutableStateFlow("")
    val adminName = MutableStateFlow("")
    val partnershipStartDate = MutableStateFlow("")
    val partnershipEndDate = MutableStateFlow("")

    private val _benefitItems = MutableStateFlow<List<BenefitItem>>(listOf(BenefitItem()))
    val benefitItems: StateFlow<List<BenefitItem>> = _benefitItems.asStateFlow()

    sealed interface WritePartnershipUiState {
        data object Idle : WritePartnershipUiState
        data object Loading : WritePartnershipUiState
        data class Success(val data: WriteSuggestionModel) : WritePartnershipUiState
        data class Fail(val code: Int, val message: String?) : WritePartnershipUiState
        data class Error(val message: String) : WritePartnershipUiState
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

    private val _writePartnershipState = MutableStateFlow<WritePartnershipUiState>(
        WritePartnershipUiState.Idle
    )
    val writePartnershipState: StateFlow<WritePartnershipUiState> =
        _writePartnershipState.asStateFlow()

    fun writePartnership() {
        viewModelScope.launch {
            val optionsDto = _benefitItems.value.map { benefit ->
                PartnershipOptionRequestDto(
                    optionType = benefit.optionType,
                    criterionType = benefit.criterionType,
                    people = if (benefit.criterionType == CriterionType.HEADCOUNT) benefit.criterionValue.toIntOrNull() else null,
                    cost = if (benefit.criterionType == CriterionType.PRICE) benefit.criterionValue.toLongOrNull() else null,
                    category = benefit.category,
                    discountRate = benefit.discountRate,
                    goods = benefit.goods.map { PartnershipGoodsRequestDto(it) }
                )
            }

            val requestDto = WritePartnershipRequestDto(
                adminId = 1L,
                partnershipPeriodStart = partnershipStartDate.value,
                partnershipPeriodEnd = partnershipEndDate.value,
                options = optionsDto
            )

            writePartnershipUseCase(requestDto)
                .onSuccess { _writePartnershipState.value = WritePartnershipUiState.Success(it) }
                .onFail { code ->
                    _writePartnershipState.value =
                        WritePartnershipUiState.Fail(code, "제휴 제안서 등록 실패")
                }
        }
    }
}