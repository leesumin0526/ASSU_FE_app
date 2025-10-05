package com.assu.app.domain.model.usage

import com.assu.app.data.dto.usage.ServiceRecord

data class MonthUsageModel (
    val serviceCount: Long,
    val records: List<ServiceRecord>
)