package com.assu.app.domain.usecase.suggestion

import com.assu.app.data.repository.suggestion.SuggestionRepository
import javax.inject.Inject

class GetSuggestionsUseCase @Inject constructor(
    private val repo: SuggestionRepository
) {
    suspend operator fun invoke() = repo.getSuggestions()
}