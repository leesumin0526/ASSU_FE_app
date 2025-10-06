package com.ssu.assu.data.dto.partnership.response

import com.ssu.assu.domain.model.admin.GetProposalAdminListModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetProposalAdminListResponseDto(
    val partnershipId: Long,
    val partnershipPeriodStart: String,
    val partnershipPeriodEnd: String,
    val adminId: Long,
    val partnerId: Long,
    val storeId: Long,
    val adminName: String,
    val options: List<PartnershipOption>
) {
    fun toModel() = GetProposalAdminListModel(
        partnershipId = this.partnershipId,
        partnershipPeriodStart = org.threeten.bp.LocalDate.parse(partnershipPeriodStart),
        partnershipPeriodEnd = org.threeten.bp.LocalDate.parse(partnershipPeriodEnd),
        adminId = this.adminId,
        partnerId = this.partnerId,
        storeId = this.storeId,
        adminName = this.adminName,
        options = this.options.map { it.toModel() }
    )
}