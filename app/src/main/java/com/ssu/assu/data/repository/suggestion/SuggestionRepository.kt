package com.ssu.assu.data.repository.suggestion

import com.ssu.assu.data.dto.suggestion.request.WriteSuggestionRequestDto
import com.ssu.assu.domain.model.suggestion.SuggestionModel
import com.ssu.assu.domain.model.suggestion.SuggestionTargetModel
import com.ssu.assu.domain.model.suggestion.WriteSuggestionModel
import com.ssu.assu.util.RetrofitResult

interface SuggestionRepository {
    suspend fun writeSuggestion(request: WriteSuggestionRequestDto): RetrofitResult<WriteSuggestionModel>

    suspend fun getSuggestionAdmins(): RetrofitResult<List<SuggestionTargetModel>>

    suspend fun getSuggestions(): RetrofitResult<List<SuggestionModel>>
}