package com.assu.app.domain.model.location

data class AdminOnMap(
    val adminId: Long?,
    val name: String,
    val address: String?,
    val partnered: Boolean,
    val partnershipId: Long?,
    val partnershipStartDate: String?,
    val partnershipEndDate: String?,
    val latitude: Double,
    val longitude: Double,
    val profileUrl: String?,
    val phoneNum: String?
)