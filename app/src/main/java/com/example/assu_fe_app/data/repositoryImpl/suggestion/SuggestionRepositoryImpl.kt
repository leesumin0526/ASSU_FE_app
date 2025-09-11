package com.example.assu_fe_app.data.repositoryImpl.suggestion

import com.example.assu_fe_app.data.dto.suggestion.request.WriteSuggestionRequestDto
import com.example.assu_fe_app.data.repository.suggestion.SuggestionRepository
import com.example.assu_fe_app.data.service.suggestion.SuggestionService
import com.example.assu_fe_app.di.ServiceModule
import com.example.assu_fe_app.domain.model.suggestion.SuggestionModel
import com.example.assu_fe_app.domain.model.suggestion.SuggestionTargetModel
import com.example.assu_fe_app.domain.model.suggestion.WriteSuggestionModel
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
import retrofit2.Retrofit
import java.io.IOException
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