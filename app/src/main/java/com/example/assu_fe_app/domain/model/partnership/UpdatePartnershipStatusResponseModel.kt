package com.example.assu_fe_app.domain.model.partnership

data class UpdatePartnershipStatusResponseModel(
    val partnershipId: Long,
    val prevStatus: String,
    val newStatus: String,
    val changedAt: String
)
