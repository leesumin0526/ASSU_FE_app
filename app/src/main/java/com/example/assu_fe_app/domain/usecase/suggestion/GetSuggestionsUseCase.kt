package com.example.assu_fe_app.domain.usecase.suggestion

import com.example.assu_fe_app.data.repository.suggestion.SuggestionRepository
import javax.inject.Inject

class GetSuggestionsUseCase @Inject constructor(
    private val repo: SuggestionRepository
) {
    suspend operator fun invoke() = repo.getSuggestions()
}