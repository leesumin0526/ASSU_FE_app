package com.example.assu_fe_app.domain.model.admin

data class GetProposalPartnerListModel(
    val shopName: String,
    val content: List<ProposalPartnerDetailsModel>,
    val startDate: String,
    val endDate: String,
)
