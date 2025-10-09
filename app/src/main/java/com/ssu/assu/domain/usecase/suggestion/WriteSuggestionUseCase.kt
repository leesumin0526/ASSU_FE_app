package com.ssu.assu.domain.usecase.suggestion

import com.ssu.assu.data.dto.suggestion.request.WriteSuggestionRequestDto
import com.ssu.assu.data.repository.suggestion.SuggestionRepository
import javax.inject.Inject

class WriteSuggestionUseCase @Inject constructor(
    private val repo: SuggestionRepository
){
    suspend operator fun invoke(req: WriteSuggestionRequestDto) = repo.writeSuggestion(req)
}