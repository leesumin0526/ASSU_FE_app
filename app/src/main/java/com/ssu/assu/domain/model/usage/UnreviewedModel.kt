package com.ssu.assu.domain.model.usage

import com.ssu.assu.data.dto.usage.ServiceRecord

data class UnreviewedModel (
    val records: List<ServiceRecord>,
    val isLastPage: Boolean
)