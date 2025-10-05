package com.assu.app.data.dto.partnership.response

import com.assu.app.domain.model.partnership.PartnershipGoodsModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoodsResponseDto(
    val goodsId: Long?,
    val goodsName: String?,
    val price: Int?,            // 없으면 제거
    val description: String?    // 없으면 제거
) {
    fun toModel() = PartnershipGoodsModel(
        goodsId = goodsId ?: -1L,
        goodsName = goodsName.orEmpty(),
    )
}