package com.ssu.assu.domain.model.admin

import com.ssu.assu.domain.model.partnership.PartnershipOptionModel

data class GetProposalAdminListModel(
    val partnershipId: Long,
    val partnershipPeriodStart: org.threeten.bp.LocalDate,
    val partnershipPeriodEnd: org.threeten.bp.LocalDate,
    val adminId: Long,
    val partnerId: Long,
    val storeId: Long,
    val adminName: String,
    val options: List<PartnershipOptionModel>
)
