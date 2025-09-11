package com.example.assu_fe_app.data.repository.suggestion

import com.example.assu_fe_app.data.dto.suggestion.request.WriteSuggestionRequestDto
import com.example.assu_fe_app.domain.model.suggestion.SuggestionModel
import com.example.assu_fe_app.domain.model.suggestion.SuggestionTargetModel
import com.example.assu_fe_app.domain.model.suggestion.WriteSuggestionModel
import com.example.assu_fe_app.util.RetrofitResult

interface SuggestionRepository {
    suspend fun writeSuggestion(request: WriteSuggestionRequestDto): RetrofitResult<WriteSuggestionModel>

    suspend fun getSuggestionAdmins(): RetrofitResult<List<SuggestionTargetModel>>

    suspend fun getSuggestions(): RetrofitResult<List<SuggestionModel>>
}