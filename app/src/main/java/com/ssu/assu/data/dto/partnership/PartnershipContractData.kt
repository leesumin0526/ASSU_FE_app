package com.ssu.assu.data.dto.partnership

import com.ssu.assu.data.dto.partner_admin.home.PartnershipContractItem
import java.io.Serializable

data class PartnershipContractData(
    val partnerName: String ?,
    val adminName: String?,
    val options: List<PartnershipContractItem>?,
    val periodStart: String?,
    val periodEnd: String?
): Serializable
