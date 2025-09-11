package com.example.assu_fe_app.data.dto.suggestion.response

import com.example.assu_fe_app.domain.model.suggestion.SuggestionModel
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

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