package com.ssu.assu.domain.model.user

import com.ssu.assu.data.dto.partnership.CriterionType
import com.ssu.assu.data.dto.partnership.OptionType

data class GetUsablePartnershipModel(
    val partnershipId: Long,
    val adminName: String,
    val partnerName: String,
    val criterionType: CriterionType,
    val optionType: OptionType,
    val people: Int,
    val cost: Long,
    val category: String,
    val description: String,
    val discountRate: Long,
    val note: String,
    val paperId: Long?
)
