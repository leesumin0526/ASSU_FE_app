package com.assu.app.domain.model.usage

import com.assu.app.data.dto.usage.ServiceRecord

data class UnreviewedModel (
    val records: List<ServiceRecord>,
    val isLastPage: Boolean
)