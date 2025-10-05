package com.assu.app.domain.model.admin

    data class RecommendedPartnerModel(
        val partnerId: Long,
        val partnerName: String,
        val partnerAddress: String,
        val partnerDetailAddress: String,
        val partnerUrl: String? = null,
        val partnerPhone: String? = null,
    ) {
        val fullAddress: String
            get() = "$partnerAddress $partnerDetailAddress"
    }
