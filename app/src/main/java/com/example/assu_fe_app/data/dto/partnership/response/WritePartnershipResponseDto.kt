package com.example.assu_fe_app.data.dto.partnership.response

import com.example.assu_fe_app.domain.model.partnership.ProposalPartnerDetailsModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WritePartnershipResponseDto(
    val partnershipId: Long,
    val partnershipPeriodStart: String?,
    val partnershipPeriodEnd: String?,
    val adminId: Long?,
    val partnerId: Long?,
    val storeId: Long?,
    val options: List<PartnershipOptionResponseDto>?
) {
    fun toModel() = ProposalPartnerDetailsModel(
        partnershipId = partnershipId,
        periodStart   = partnershipPeriodStart.orEmpty(),
        periodEnd     = partnershipPeriodEnd.orEmpty(),
        adminId       = adminId,
        partnerId     = partnerId,
        storeId       = storeId,
        options       = (options ?: emptyList()).map { it.toModel() }
    )
}

