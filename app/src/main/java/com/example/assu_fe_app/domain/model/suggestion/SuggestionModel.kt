package com.example.assu_fe_app.domain.model.suggestion

data class SuggestionModel(
    val suggestionId: Long,
    val storeName: String,
    val departmentInfo: String,
    val status: String,
    val content: String,
    val date: String
)
