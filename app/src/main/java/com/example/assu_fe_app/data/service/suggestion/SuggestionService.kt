package com.example.assu_fe_app.data.service.suggestion

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.suggestion.request.WriteSuggestionRequestDto
import com.example.assu_fe_app.data.dto.suggestion.response.GetSuggestionResponseDto
import com.example.assu_fe_app.data.dto.suggestion.response.SuggestionTargetInfoDto
import com.example.assu_fe_app.data.dto.suggestion.response.WriteSuggestionResponseDto
import com.example.assu_fe_app.domain.model.suggestion.SuggestionTargetModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SuggestionService {

    // 제휴 건의 작성 API
    @POST("suggestion")
    suspend fun writeSuggestion(
        @Body request: WriteSuggestionRequestDto
    ): BaseResponse<WriteSuggestionResponseDto>

    // 제휴 건의대상 조회 API
    @GET("suggestion/admin")
    suspend fun getSuggestionAdmins(): BaseResponse<SuggestionTargetInfoDto>

    // 제휴 건의 조회 API
    @GET("suggestion/list")
    suspend fun getSuggestions(): BaseResponse<List<GetSuggestionResponseDto>>
}