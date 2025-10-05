package com.assu.app.data.dto.partnership.response

import com.assu.app.domain.model.partnership.PartnershipOptionModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PartnershipOptionResponseDto(
    val optionType: OptionType,     // enum이면 String 유지
    val criterionType: CriterionType,  // enum이면 String 유지
    val people: Int?,
    val cost: Long?,
    val category: String?,
    val discountRate: Long?,
    val goods: List<PartnershipGoodsResponseDto>
) {
    fun toModel() = PartnershipOptionModel(
        optionType = optionType,
        criterionType = criterionType,
        people = people ?: 0,
        cost = cost ?: 0,
        category = category.orEmpty(),
        discountRate = discountRate ?: 0,
        goods = goods.map { it.toModel() }
    )
}
