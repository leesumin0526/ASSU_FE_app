package com.example.assu_fe_app.domain.usecase.suggestion

import com.example.assu_fe_app.data.repository.suggestion.SuggestionRepository
import com.example.assu_fe_app.domain.model.suggestion.SuggestionTargetModel
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class GetSuggestionAdminsUseCase @Inject constructor(
    private val repo: SuggestionRepository
) {
    suspend operator fun invoke() = repo.getSuggestionAdmins()
}