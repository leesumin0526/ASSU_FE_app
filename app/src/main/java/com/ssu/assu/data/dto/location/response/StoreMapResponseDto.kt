package com.ssu.assu.data.dto.location.response

import com.ssu.assu.domain.model.location.StoreOnMap
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StoreMapResponseDto(
    val storeId: Long? = null,
    val adminId: Long? = null,
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
    val profileUrl: String? = null,
    val phoneNumber: String? = null
) {
    fun toModel() = StoreOnMap(
        storeId = this.storeId,
        adminId = this.adminId,
        name = this.name,
        address = this.address,
        rate = this.rate,
        criterionType = this.criterionType,
        optionType = this.optionType,
        people = this.people,
        cost = this.cost,
        category = this.category,
        discountRate = this.discountRate,
        hasPartner = this.hasPartner,
        latitude = this.latitude,
        longitude = this.longitude,
        profileUrl = this.profileUrl,
        phoneNum = this.phoneNumber
    )
}