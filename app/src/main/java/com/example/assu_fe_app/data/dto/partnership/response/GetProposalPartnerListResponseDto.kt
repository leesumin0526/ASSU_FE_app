package com.example.assu_fe_app.data.dto.partnership.response

import com.example.assu_fe_app.domain.model.admin.GetProposalPartnerListModel
import com.example.assu_fe_app.domain.model.admin.ProposalPartnerDetailsModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetProposalPartnerListResponseDto(
    val shopName: String,
    val content: List<ProposalPartnerDetails>,
    val startDate: String,
    val endDate: String,
) {
    fun toModel() = GetProposalPartnerListModel (
        shopName = this.shopName,
        content = this.content.map{it.toModel()},
        startDate = this.startDate,
        endDate = this.endDate,
    )
}

@JsonClass(generateAdapter = true)
data class ProposalPartnerDetails (
    val type: String,
    val people: Long,
    val cost: Long?,      // nullable 처리
    val discount: Long?,  // nullable 처리
    val goods: List<String>
) {
    fun toModel() = ProposalPartnerDetailsModel (
        type = this.type,
        people = this.people,
        cost = this.cost,
        discount = this.discount,
        goods = this.goods
    )
}
