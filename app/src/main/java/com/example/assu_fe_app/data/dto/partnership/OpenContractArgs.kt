package com.example.assu_fe_app.data.dto.partnership

import java.io.Serializable

data class OpenContractArgs(
    val partnershipId: Long,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val adminName: String? = null,
    val partnerName: String? = null,
    val term: String? = null,
    val profileUrl: String? = null
) : Serializable