package com.example.assu_fe_app.data.dto

data class ProposalItem(
    var num: String = "",
    var content: String = "",
    var condition: Int = CONDITION_COST
) {
    companion object {
        const val CONDITION_COST = 0
        const val CONDITION_PEOPLE = 1
    }
}