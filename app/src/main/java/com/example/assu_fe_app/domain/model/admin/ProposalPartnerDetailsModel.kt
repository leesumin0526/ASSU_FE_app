package com.example.assu_fe_app.domain.model.admin

data class ProposalPartnerDetailsModel(
    val type: String,
    val people: Long,
    val cost: Long?,      // nullable 처리
    val discount: Long?,  // nullable 처리
    val goods: List<String>
)
