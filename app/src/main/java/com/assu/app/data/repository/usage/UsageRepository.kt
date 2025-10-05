package com.assu.app.data.repository.usage

import com.assu.app.data.dto.certification.response.NoneDataResponseDto
import com.assu.app.data.dto.usage.SaveUsageRequestDto
import com.assu.app.domain.model.usage.MonthUsageModel
import com.assu.app.domain.model.usage.UnreviewedModel
import com.assu.app.util.RetrofitResult

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
    ) : RetrofitResult<NoneDataResponseDto>


}