package com.assu.app.data.dto.partnership.response

import com.assu.app.domain.model.partnership.UpdatePartnershipStatusResponseModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdatePartnershipStatusResponseDto(
    val partnershipId: Long,
    val prevStatus: String,
    val newStatus: String,
    val changedAt: String
) {
    fun toModel() = UpdatePartnershipStatusResponseModel(
        partnershipId = this.partnershipId,
        prevStatus = this.prevStatus,
        newStatus = this.newStatus,
        changedAt = this.changedAt
    )
}
