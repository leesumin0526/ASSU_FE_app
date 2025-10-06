package com.ssu.assu.data.dto

enum class OfferType {
    SERVICE,
    DISCOUNT
}

data class ProposalItem(
    var offerType: OfferType = OfferType.SERVICE,
    var num: String = "",
    var content: String = "",
    var condition: Int = CONDITION_COST,
    var placeholder: String = "캔콜라",     // 기본 힌트
    var least: String = "10,000", // 기본 힌트 2
    var contents: MutableList<String> = mutableListOf(""),  // “캔콜라” 같은 문자열 리스트
    var category: String? = null
) {
    companion object {
        const val CONDITION_COST = 0
        const val CONDITION_PEOPLE = 1
    }
}