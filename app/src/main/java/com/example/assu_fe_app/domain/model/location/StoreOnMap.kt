package com.example.assu_fe_app.domain.model.location

data class StoreOnMap(
    val storeId: Long?,
    val adminId: Long?,
    val name: String,
    val address: String?,
    val rate: Double?,
    val criterionType: String?,
    val optionType: String?,
    val people: Int?,
    val cost: Int?,
    val category: String?,
    val discountRate: Int?,
    val hasPartner: Boolean,
    val latitude: Double,
    val longitude: Double,
    val profileUrl: String?,
    val phoneNum: String?
)