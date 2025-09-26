package com.example.assu_fe_app.domain.model.partnership

data class ManualPartnershipModel(
    val storeId: Long,
    val storeCreated: Boolean,
    val storeActivated: Boolean,
    val status: String?,
    val contractImageUrl: String?
)