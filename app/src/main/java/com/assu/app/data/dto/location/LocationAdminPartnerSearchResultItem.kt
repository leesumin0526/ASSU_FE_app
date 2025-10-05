package com.assu.app.data.dto.location

data class LocationAdminPartnerSearchResultItem(
    val id: Long?, // 상대 id -> admin 화면일 경우 partnerId를 partnerId일 경우 adminId를 반환합니다.
    val shopName: String,
    val address: String,
    val partnered: Boolean,
    val paperId: Long?,
    val term: String?,
    val partnershipStartDate: String?,
    val partnershipEndDate: String?,
    val latitude: Double,
    val longitude: Double,
    val partnershipId: Long? = null,
    val profileUrl: String? = null,
    val phoneNumber: String? = null
)
