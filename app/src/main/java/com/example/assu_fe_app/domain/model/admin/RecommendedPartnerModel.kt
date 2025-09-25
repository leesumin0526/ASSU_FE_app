package com.example.assu_fe_app.domain.model.admin

    data class RecommendedPartnerModel(
        val partnerId: Long,
        val partnerName: String,
        val partnerAddress: String,
        val partnerDetailAddress: String
    ) {
        val fullAddress: String
            get() = "$partnerAddress $partnerDetailAddress"
    }
