package com.assu.app.data.dto.partnership.response

import com.assu.app.domain.model.partnership.PartnershipGoodsModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PartnershipGoodsResponseDto(
    val goodsId: Long,
    val goodsName: String
) {
    fun toModel() = PartnershipGoodsModel(
        goodsId = goodsId,
        goodsName = goodsName
    )
}
