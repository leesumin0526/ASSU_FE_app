package com.example.assu_fe_app.data.dto.partnership

import com.example.assu_fe_app.data.dto.partner_admin.home.PartnershipContractItem
import java.io.Serializable

data class PartnershipContractData(
    val partnerName: String ?,
    val adminName: String?,
    val options: List<PartnershipContractItem>?,
    val periodStart: String?,
    val periodEnd: String?
): Serializable
