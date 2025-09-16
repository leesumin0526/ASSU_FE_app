package com.example.assu_fe_app.data.dto.location.response

import com.example.assu_fe_app.domain.model.location.AdminOnMap
import com.squareup.moshi.JsonClass
import kotlin.String

@JsonClass(generateAdapter = true)
data class AdminMapResponseDto(
    val adminId: Long? = null,
    val name: String,
    val address: String?,
    val partnered: Boolean,
    val partnershipId: Long?,
    val partnershipStartDate: String?,
    val partnershipEndDate: String?,
    val latitude: Double,
    val longitude: Double
) {
    fun toModel() = AdminOnMap(
        adminId = this.adminId,
        name = this.name,
        address = this.address,
        partnered = this.partnered,
        partnershipId = this.partnershipId,
        partnershipStartDate = this.partnershipStartDate,
        partnershipEndDate = this.partnershipEndDate,
        latitude = this.latitude,
        longitude = this.longitude
    )
}