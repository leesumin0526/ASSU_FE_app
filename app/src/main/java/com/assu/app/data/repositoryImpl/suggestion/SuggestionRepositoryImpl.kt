package com.assu.app.data.repositoryImpl.suggestion

import com.assu.app.data.dto.suggestion.request.WriteSuggestionRequestDto
import com.assu.app.data.repository.suggestion.SuggestionRepository
import com.assu.app.data.service.suggestion.SuggestionService
import com.assu.app.domain.model.suggestion.SuggestionModel
import com.assu.app.domain.model.suggestion.SuggestionTargetModel
import com.assu.app.domain.model.suggestion.WriteSuggestionModel
import com.assu.app.util.RetrofitResult
import com.assu.app.util.apiHandler
import javax.inject.Inject

class SuggestionRepositoryImpl @Inject constructor(
    private val api: SuggestionService
) : SuggestionRepository {

    override suspend fun writeSuggestion(
        request: WriteSuggestionRequestDto
    ): RetrofitResult<WriteSuggestionModel> {
        return apiHandler(
            { api.writeSuggestion(request) },
            { dto -> dto.toModel() }
        )
    }

    override suspend fun getSuggestionAdmins(): RetrofitResult<List<SuggestionTargetModel>> {
        return apiHandler(
            { api.getSuggestionAdmins() },
            { dto -> dto.toModel() }
        )
    }

    override suspend fun getSuggestions(): RetrofitResult<List<SuggestionModel>> {
        return apiHandler(
            { api.getSuggestions() },
            { dtoList -> dtoList.map { it.toModel() } }
        )
    }
}