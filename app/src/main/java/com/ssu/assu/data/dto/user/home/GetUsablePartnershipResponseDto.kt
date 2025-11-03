package com.ssu.assu.data.dto.user.home

import com.squareup.moshi.JsonClass
import com.ssu.assu.data.dto.partnership.CriterionType
import com.ssu.assu.data.dto.partnership.OptionType
import com.ssu.assu.domain.model.user.GetUsablePartnershipModel

@JsonClass(generateAdapter = true)
data class GetUsablePartnershipResponseDto(
    val partnershipId: Long,
    val adminName: String,
    val partnerName: String,
    val criterionType: CriterionType?,
    val optionType: OptionType?,
    val people: Int?,
    val cost: Long?,
    val category: String?,
    val description: String?,
    val discountRate: Long?,
    val note: String?,
    val paperId: Long?
) {
    fun toModel() = GetUsablePartnershipModel(
        partnershipId = this.partnershipId,
        adminName = this.adminName,
        partnerName = this.partnerName,
        criterionType = this.criterionType?: CriterionType.PRICE,
        optionType = this.optionType?: OptionType.DISCOUNT,
        people = this.people?:0,
        cost = this.cost?:0,
        category = this.category?:"",
        description = this.description?:"",
        discountRate = this.discountRate?:0,
        note = this.note?:"아직 준비되지 않았어요!",
        paperId = this.paperId?:-1
    )
}
