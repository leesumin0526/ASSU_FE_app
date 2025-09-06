package com.example.assu_fe_app.data.dto.store

data class PaperContent(
    val adminName: String,
    val contentId: Long,
    val goods: List<String>,
    val paperContent: String,
    val people: Int
)