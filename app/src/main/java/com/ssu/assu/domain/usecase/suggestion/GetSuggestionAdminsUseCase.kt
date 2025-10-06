package com.ssu.assu.domain.usecase.suggestion

import com.ssu.assu.data.repository.suggestion.SuggestionRepository
import javax.inject.Inject

class GetSuggestionAdminsUseCase @Inject constructor(
    private val repo: SuggestionRepository
) {
    suspend operator fun invoke() = repo.getSuggestionAdmins()
}