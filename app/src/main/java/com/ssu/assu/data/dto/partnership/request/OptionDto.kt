package com.ssu.assu.data.dto.partnership.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OptionDto(
    val optionType: String,        // "SERVICE" | "DISCOUNT"
    val criterionType: String,     // "PRICE" | "HEADCOUNT"
    val people: Int?,              // 기준이 인원일 때 값
    val cost: Long?,               // 기준이 금액일 때 값 (Long 권장)
    val category: String?,         // 카테고리(있으면)
    val discountRate: Int?,        // 할인율(%), DISCOUNT일 때
    val goods: List<GoodsRequestDto>? // SERVICE일 때 제공 품목 리스트
)

@JsonClass(generateAdapter = true)
data class GoodsRequestDto(
    val goodsName: String
)