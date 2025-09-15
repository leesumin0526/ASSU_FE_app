package com.example.assu_fe_app.domain.model.partnership

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PartnershipStatusModel(
    val paperId: Long?,
    val isPartnered: Boolean?,
    val status: String,
    val opponentId: Long?,
    val opponentName: String?,
    val opponentAddress: String?
) : Parcelable
