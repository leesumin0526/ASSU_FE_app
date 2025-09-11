package com.example.assu_fe_app.data.repository.usage

import com.example.assu_fe_app.data.dto.usage.SaveUsageRequestDto
import com.example.assu_fe_app.data.dto.usage.response.GetUnreviewedUsageDto
import com.example.assu_fe_app.data.dto.usage.ServiceRecord
import com.example.assu_fe_app.data.dto.usage.response.SaveUsageResponseDto
import com.example.assu_fe_app.data.dto.usage.response.UserMonthUsageResponseDto
import com.example.assu_fe_app.domain.model.usage.MonthUsageModel
import com.example.assu_fe_app.domain.model.usage.UnreviewedModel
import com.example.assu_fe_app.util.RetrofitResult

interface UsageRepository {

    suspend fun getMonthUsage(
        year : Int,
        month : Int
    ) : RetrofitResult<MonthUsageModel>

    suspend fun getUnreviewedUsage(
        page: Int,
        size: Int,
        sort: String
    ) : RetrofitResult<UnreviewedModel>

    suspend fun postUsage(
        request : SaveUsageRequestDto
    ) : RetrofitResult<SaveUsageResponseDto>


}