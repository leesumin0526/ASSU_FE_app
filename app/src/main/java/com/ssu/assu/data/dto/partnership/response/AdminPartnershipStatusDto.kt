package com.ssu.assu.data.dto.partnership.response

import com.ssu.assu.domain.model.partnership.PartnershipStatusModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AdminPartnershipStatusDto(
    val paperId: Long?,
    val isPartnered: Boolean?,
    val status: String,
    val partnerId: Long?,
    val partnerName: String?,
    val partnerAddress: String?
) {
    fun toModel() = PartnershipStatusModel(
        paperId = this.paperId,
        isPartnered = this.isPartnered,
        status = this.status,
        opponentId = this.partnerId,
        opponentName = this.partnerName,
        opponentAddress = this.partnerAddress
    )
}
