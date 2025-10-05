package com.assu.app.domain.model.admin

import com.assu.app.domain.model.partnership.PartnershipOptionModel

data class GetProposalPartnerListModel(
    val partnershipId: Long,
    val partnershipPeriodStart: org.threeten.bp.LocalDate,
    val partnershipPeriodEnd: org.threeten.bp.LocalDate,
    val adminId: Long,
    val partnerId: Long,
    val storeId: Long,
    val storeName: String,
    val adminName: String,
    val options: List<PartnershipOptionModel>
)
