package com.example.assu_fe_app.data.dto.store

data class StorePartnershipResponseDto(
    val contents: List<PaperContent>,
    val storeId: Long,
    val storeName: String
)