package com.example.assu_fe_app.ui.partnership

import com.example.assu_fe_app.data.dto.partnership.CriterionType
import com.example.assu_fe_app.data.dto.partnership.OptionType

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