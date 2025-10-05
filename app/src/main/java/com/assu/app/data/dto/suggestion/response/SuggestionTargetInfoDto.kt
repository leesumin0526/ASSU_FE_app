package com.assu.app.data.dto.suggestion.response

import com.assu.app.domain.model.suggestion.SuggestionTargetModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SuggestionTargetInfoDto(
    val adminId: Long?,
    val adminName: String?,
    val departId: Long?,
    val departName: String?,
    val majorId: Long?,
    val majorName: String?
) {
    fun toModel(): List<SuggestionTargetModel> {
        val targetList = mutableListOf<SuggestionTargetModel>()
        adminId?.let { id -> targetList.add(SuggestionTargetModel(id, adminName ?: "총학생회")) }
        departId?.let { id -> targetList.add(SuggestionTargetModel(id, departName ?: "단과대학")) }
        majorId?.let { id -> targetList.add(SuggestionTargetModel(id, majorName ?: "학부/학과")) }
        return targetList
    }
}
