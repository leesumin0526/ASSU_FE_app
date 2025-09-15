package com.example.assu_fe_app.domain.model.usage

import com.example.assu_fe_app.data.dto.usage.ServiceRecord

data class UnreviewedModel (
    val records: List<ServiceRecord>,
    val isLastPage: Boolean
)