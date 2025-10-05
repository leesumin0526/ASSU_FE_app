package com.assu.app.ui.partnership

import com.assu.app.data.dto.partnership.CriterionType
import com.assu.app.data.dto.partnership.OptionType
import com.assu.app.domain.model.partnership.WritePartnershipResponseModel

sealed interface BenefitItemEvent {
    data class OptionTypeChanged(val newType: OptionType) : BenefitItemEvent
    data class CriterionTypeChanged(val newType: CriterionType) : BenefitItemEvent
    data class CriterionValueChanged(val value: String) : BenefitItemEvent
    data class CategoryChanged(val text: String) : BenefitItemEvent
    data class DiscountRateChanged(val rate: String) : BenefitItemEvent
    data object GoodAdded : BenefitItemEvent
    data class GoodRemoved(val goodIndex: Int) : BenefitItemEvent
    data class GoodUpdated(val goodIndex: Int, val text: String) : BenefitItemEvent
    data object ItemRemoved : BenefitItemEvent
}

sealed interface WritePartnershipUiState {
    data object Idle : WritePartnershipUiState // 초기 상태
    data object Loading : WritePartnershipUiState // 로딩 중
    data class Success(val data: WritePartnershipResponseModel) : WritePartnershipUiState // 성공
    data class Fail(val code: Int, val message: String?) : WritePartnershipUiState // 서버 실패 (4xx, 5xx 등)
    data class Error(val message: String) : WritePartnershipUiState // 네트워크 등 기타 에러
}