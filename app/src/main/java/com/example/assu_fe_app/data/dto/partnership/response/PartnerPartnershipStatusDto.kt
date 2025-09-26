package com.example.assu_fe_app.data.dto.partnership.response

import com.example.assu_fe_app.domain.model.partnership.PartnershipStatusModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PartnerPartnershipStatusDto(
    val paperId: Long?,
    val isPartnered: Boolean?,
    val status: String,
    val adminId: Long?,
    val adminName: String?,
    val adminAddress: String?
) {
    fun toModel() = PartnershipStatusModel(
        paperId = this.paperId,
        isPartnered = this.isPartnered,
        status = this.status,
        opponentId = adminId,
        opponentName = adminName,
        opponentAddress = adminAddress
    )
}
