package com.example.assu_fe_app.data.dto.location.response

import com.example.assu_fe_app.domain.model.location.PartnerOnMap
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PartnerMapResponseDto(
    @Json(name = "partnerId") val partnerId: Long? = null,
    @Json(name = "name") val name: String,
    @Json(name = "address") val address: String?,
    @Json(name = "partnered") val partnered: Boolean = false,
    @Json(name = "partnershipId") val partnershipId: Long?,
    @Json(name = "partnershipStartDate") val partnershipStartDate: String?,
    @Json(name = "partnershipEndDate") val partnershipEndDate: String?,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    val profileUrl: String? = null
) {
    fun toModel() = PartnerOnMap(
        partnerId = partnerId,
        shopName = name,
        address = address,
        partnered = partnered,
        partnershipId = partnershipId,
        partnershipStartDate = partnershipStartDate,
        partnershipEndDate = partnershipEndDate,
        latitude = latitude,
        longitude = longitude,
        profileUrl = profileUrl
    )
}