package com.example.assu_fe_app.data.dto.suggestion.response

import com.example.assu_fe_app.domain.model.suggestion.SuggestionTargetModel
import com.example.assu_fe_app.domain.model.suggestion.WriteSuggestionModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class WriteSuggestionResponseDto (
    val suggestionId: Long,
    val userId: Long,
    val adminId: Long,
    val storeName: String,
    val suggestionBenefit: String
) {
    fun toModel() = WriteSuggestionModel(
        suggestionId = this.suggestionId,
        storeName = this.storeName
    )
}