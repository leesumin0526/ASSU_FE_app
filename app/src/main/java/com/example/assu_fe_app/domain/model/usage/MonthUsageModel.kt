package com.example.assu_fe_app.domain.model.usage

import com.example.assu_fe_app.data.dto.usage.ServiceRecord

data class MonthUsageModel (
    val serviceCount: Long,
    val records: List<ServiceRecord>
)