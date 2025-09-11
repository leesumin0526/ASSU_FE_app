package com.example.assu_fe_app.domain.usecase.suggestion

import com.example.assu_fe_app.data.dto.suggestion.request.WriteSuggestionRequestDto
import com.example.assu_fe_app.data.dto.suggestion.response.WriteSuggestionResponseDto
import com.example.assu_fe_app.data.repository.suggestion.SuggestionRepository
import com.example.assu_fe_app.domain.model.suggestion.WriteSuggestionModel
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class WriteSuggestionUseCase @Inject constructor(
    private val repo: SuggestionRepository
){
    suspend operator fun invoke(req: WriteSuggestionRequestDto) = repo.writeSuggestion(req)
}