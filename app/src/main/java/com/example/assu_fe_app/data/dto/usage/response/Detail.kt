package com.example.assu_fe_app.data.dto.usage.response

import java.time.LocalDateTime

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