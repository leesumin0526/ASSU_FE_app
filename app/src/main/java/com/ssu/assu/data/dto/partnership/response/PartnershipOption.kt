package com.ssu.assu.data.dto.partnership.response

import com.ssu.assu.domain.model.partnership.PartnershipGoodsModel
import com.ssu.assu.domain.model.partnership.PartnershipOptionModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PartnershipOption (
    val optionType: OptionType,
    val criterionType: CriterionType,
    val people: Int?,
    val cost: Long?,
    val category: String?,
    val discountRate: Long?,
    val goods: List<PartnershipGoods>?
) {
    fun toModel() = PartnershipOptionModel(
        optionType = this.optionType,
        criterionType = this.criterionType,
        people = this.people ?: 0,
        cost = this.cost ?: 0L,
        category = this.category ?:"",
        discountRate = this.discountRate ?: 0L,
        goods = this.goods?.map { it.toModel() } ?: emptyList()
    )
}

@JsonClass(generateAdapter = true)
data class PartnershipGoods (
    val goodsId: Long,
    val goodsName: String,
) {
    fun toModel() = PartnershipGoodsModel(
        goodsId = this.goodsId,
        goodsName = this.goodsName,
    )
}

enum class OptionType {
    SERVICE,DISCOUNT
}

enum class CriterionType {
    PRICE, HEADCOUNT
}