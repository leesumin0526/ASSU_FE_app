package com.ssu.assu.data.dto.suggestion.response

import com.ssu.assu.domain.model.suggestion.SuggestionModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetSuggestionResponseDto(
    val suggestionId: Long,
    val createdAt: String,
    val storeName: String,
    val content: String,
    val studentMajor: String,
    val enrollmentStatus: String
) {
    fun toModel(): SuggestionModel {
        return SuggestionModel(
            suggestionId = this.suggestionId,
            storeName = this.storeName,
            departmentInfo = this.studentMajor,
            status = this.enrollmentStatus,
            content = this.content,
            date = this.createdAt.substring(0, 16).replace("T", " ")
        )
    }
}