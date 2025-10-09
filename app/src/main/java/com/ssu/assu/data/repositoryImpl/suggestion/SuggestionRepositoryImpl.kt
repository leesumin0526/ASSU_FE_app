package com.ssu.assu.data.repositoryImpl.suggestion

import com.ssu.assu.data.dto.suggestion.request.WriteSuggestionRequestDto
import com.ssu.assu.data.repository.suggestion.SuggestionRepository
import com.ssu.assu.data.service.suggestion.SuggestionService
import com.ssu.assu.domain.model.suggestion.SuggestionModel
import com.ssu.assu.domain.model.suggestion.SuggestionTargetModel
import com.ssu.assu.domain.model.suggestion.WriteSuggestionModel
import com.ssu.assu.util.RetrofitResult
import com.ssu.assu.util.apiHandler
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