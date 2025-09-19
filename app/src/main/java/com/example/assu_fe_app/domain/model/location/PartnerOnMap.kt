package com.example.assu_fe_app.domain.model.location

data class PartnerOnMap(
    val partnerId: Long?,
    val shopName: String,
    val address: String?,
    val partnered: Boolean,
    val partnershipId: Long?,
    val partnershipStartDate: String?,
    val partnershipEndDate: String?,
    val latitude: Double,
    val longitude: Double,
    val profileUrl: String?
)