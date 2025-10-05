package com.assu.app.data.dto.partnership.response

import com.assu.app.domain.model.partnership.ProposalPartnerDetailsModel
import com.assu.app.domain.model.partnership.WritePartnershipResponseModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WritePartnershipResponseDto(
    val partnershipId: Long,
    val updatedAt: String?,
    val partnershipPeriodStart: String?,
    val partnershipPeriodEnd: String?,
    val adminId: Long?,
    val partnerId: Long?,
    val storeId: Long?,
    val options: List<PartnershipOptionResponseDto>?
) {
    fun toDetailModel() = ProposalPartnerDetailsModel(
        partnershipId = partnershipId,
        updatedAt     = updatedAt,
        periodStart   = partnershipPeriodStart.orEmpty(),
        periodEnd     = partnershipPeriodEnd.orEmpty(),
        adminId       = adminId,
        partnerId     = partnerId,
        storeId       = storeId,
        options       = (options ?: emptyList()).map { it.toModel() }
    )
    fun toModel() = WritePartnershipResponseModel(paperId = partnershipId)
}
