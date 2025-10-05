package com.assu.app.data.repository.suggestion

import com.assu.app.data.dto.suggestion.request.WriteSuggestionRequestDto
import com.assu.app.domain.model.suggestion.SuggestionModel
import com.assu.app.domain.model.suggestion.SuggestionTargetModel
import com.assu.app.domain.model.suggestion.WriteSuggestionModel
import com.assu.app.util.RetrofitResult

interface SuggestionRepository {
    suspend fun writeSuggestion(request: WriteSuggestionRequestDto): RetrofitResult<WriteSuggestionModel>

    suspend fun getSuggestionAdmins(): RetrofitResult<List<SuggestionTargetModel>>

    suspend fun getSuggestions(): RetrofitResult<List<SuggestionModel>>
}