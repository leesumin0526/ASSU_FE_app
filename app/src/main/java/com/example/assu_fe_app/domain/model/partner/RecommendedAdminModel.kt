package com.example.assu_fe_app.domain.model.partner


data class RecommendedAdminModel(
    val adminId: Long,
    val adminName: String,
    val adminAddress: String,
    val adminDetailAddress: String,
    val adminUrl: String? = null
) {
    val fullAddress: String
        get() = "$adminAddress $adminDetailAddress"
}