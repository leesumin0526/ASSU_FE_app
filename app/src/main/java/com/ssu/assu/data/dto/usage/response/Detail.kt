package com.ssu.assu.data.dto.usage.response

data class Detail(
    val adminName: String,
    val benefitDescription: String,
    val partnerId: Long,
    val partnershipUsageId: Long,
    val reviewed: Boolean,
    val storeId: Long,
    val storeName: String,
    val usedAt: String
)