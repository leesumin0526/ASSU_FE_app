package com.example.assu_fe_app.data.dto.usage.response

data class Content(
    val adminId: Long,
    val adminName: String,
    val contentId: Long,
    val cost: Long,
    val goods: List<String>,
    val paperContent: String,
    val people: Int
)