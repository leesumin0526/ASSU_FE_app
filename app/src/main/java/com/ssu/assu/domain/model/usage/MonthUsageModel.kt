package com.ssu.assu.domain.model.usage

import com.ssu.assu.data.dto.usage.ServiceRecord

data class MonthUsageModel (
    val serviceCount: Long,
    val records: List<ServiceRecord>
)