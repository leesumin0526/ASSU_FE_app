package com.ssu.assu.data.dto.store

data class PaperContent(
    val adminId: Long,
    val adminName: String,
    val contentId: Long,
    val goods: List<String>?,
    val paperContent: String,
    val people: Int?,
    val cost : Long?
)