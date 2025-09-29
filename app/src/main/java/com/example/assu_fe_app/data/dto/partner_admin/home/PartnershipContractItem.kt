package com.example.assu_fe_app.data.dto.partner_admin.home

sealed class PartnershipContractItem {
    sealed class Service : PartnershipContractItem() {
        data class ByPeople(
            val minPeople: Int,
            val items: String,
            val category: String? = null
            ) : Service()
        data class ByAmount(
            val minAmount: Int,
            val items: String,
            val category: String? = null
            ) : Service()
    }
    sealed class Discount : PartnershipContractItem() {
        data class ByPeople(
            val minPeople: Int,
            val percent: Int,) : Discount()
        data class ByAmount(
            val minAmount: Int,
            val percent: Int,) : Discount()
    }
}