package com.example.assu_fe_app.domain.model.admin

data class GetProposalPartnerListModel(
    val partnershipId: Long,
    val partnershipPeriodStart: org.threeten.bp.LocalDate,
    val partnershipPeriodEnd: org.threeten.bp.LocalDate,
    val adminId: Long,
    val partnerId: Long,
    val storeId: Long,
    val options: List<PartnershipOptionModel>
)
