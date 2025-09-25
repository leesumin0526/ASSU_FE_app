package com.example.assu_fe_app.presentation.admin.home.adapter

import com.example.assu_fe_app.data.dto.OfferType
import com.example.assu_fe_app.data.dto.ProposalItem
import com.example.assu_fe_app.data.dto.partnership.request.GoodsRequestDto
import com.example.assu_fe_app.data.dto.partnership.request.OptionDto

private fun String.digits() = filter(Char::isDigit)

fun ProposalItem.toOptionDtoOrThrow(): OptionDto {
    val criterionType = when (condition) {
        ProposalItem.CONDITION_COST   -> "PRICE"     // 0
        ProposalItem.CONDITION_PEOPLE -> "HEADCOUNT" // 1
        else -> error("조건(가격/인원)을 선택해 주세요.")
    }

    // ✅ 기준값은 num(문자열)에 입력 → 숫자만 추출
    val numDigits = num.digits()
    val people: Int? = if (criterionType == "HEADCOUNT")
        numDigits.toIntOrNull() ?: error("인원 기준값을 입력해 주세요.")
    else null

    val cost: Long? = if (criterionType == "PRICE")
        (numDigits.toLongOrNull()) ?: error("가격 기준값을 입력해 주세요.")
    else null

    return when (offerType) {
        OfferType.SERVICE -> {
            // ✅ 서비스: contents = [품목들...]
            val goods = contents.map { it.trim() }
                .filter { it.isNotEmpty() }
                .map { GoodsRequestDto(goodsName = it) }
                .takeIf { it.isNotEmpty() }
                ?: error("서비스 제공 품목을 1개 이상 입력해 주세요.")

            OptionDto(
                optionType = "SERVICE",
                criterionType = criterionType,
                people = people,
                cost = cost,
                category = null,
                discountRate = null,
                goods = goods
            )
        }
        OfferType.DISCOUNT -> {
            // ✅ 할인: contents[0] = 할인율(%), 1칸만 사용
            val discountRate = contents.getOrNull(0)?.trim()?.digits()?.toIntOrNull()
                ?: error("할인율(%)을 입력해 주세요.")

            OptionDto(
                optionType = "DISCOUNT",
                criterionType = criterionType,
                people = people,
                cost = cost,
                category = null,
                discountRate = discountRate,
                goods = null
            )
        }
    }
}