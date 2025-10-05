package com.assu.app.domain.model.partner


data class RecommendedAdminModel(
    val adminId: Long,
    val adminName: String,
    val adminAddress: String,
    val adminDetailAddress: String,
    val adminUrl: String? = null,
    val adminPhone: String? = null
) {
    val fullAddress: String
        get() = "$adminAddress $adminDetailAddress"
}