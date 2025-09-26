package com.example.assu_fe_app.data.dto.partnership.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ManualPartnershipRequestDto(
    val storeName: String,
    val selectedPlace: SelectedPlaceDto,
    val storeDetailAddress: String?,
    val partnershipPeriodStart: String, // yyyy-MM-dd
    val partnershipPeriodEnd: String,   // yyyy-MM-dd
    val options: List<OptionDto>
)