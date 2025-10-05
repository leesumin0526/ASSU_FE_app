package com.assu.app.domain.usecase.suggestion

import com.assu.app.data.dto.suggestion.request.WriteSuggestionRequestDto
import com.assu.app.data.repository.suggestion.SuggestionRepository
import javax.inject.Inject

class WriteSuggestionUseCase @Inject constructor(
    private val repo: SuggestionRepository
){
    suspend operator fun invoke(req: WriteSuggestionRequestDto) = repo.writeSuggestion(req)
}