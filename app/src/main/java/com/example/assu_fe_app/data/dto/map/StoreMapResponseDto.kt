package com.example.assu_fe_app.data.dto.map

data class StoreMapResponseDto(
    val address: String,
    val adminName: String,
    val adminId: Long,
    val category: String?,
    val cost: Long?,
    val criterionType: String,
    val discountRate: Long?,
    val hasPartner: Boolean,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val optionType: String,
    val people: Int?,
    val rate: Int?,
    val storeId: Long
)