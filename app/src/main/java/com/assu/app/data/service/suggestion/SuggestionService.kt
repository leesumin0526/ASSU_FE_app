package com.assu.app.data.service.suggestion

import com.assu.app.data.dto.BaseResponse
import com.assu.app.data.dto.suggestion.request.WriteSuggestionRequestDto
import com.assu.app.data.dto.suggestion.response.GetSuggestionResponseDto
import com.assu.app.data.dto.suggestion.response.SuggestionTargetInfoDto
import com.assu.app.data.dto.suggestion.response.WriteSuggestionResponseDto
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