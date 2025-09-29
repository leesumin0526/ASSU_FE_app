package com.example.assu_fe_app.domain.model.partnership

data class ProposalPartnerDetailsModel(
    val partnershipId: Long,
    val updatedAt: String? = null,
    val periodStart: String,
    val periodEnd: String,
    val adminId: Long?,
    val partnerId: Long?,
    val storeId: Long?,
    val options: List<PartnershipOptionModel>,
)