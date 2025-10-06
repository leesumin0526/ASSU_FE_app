package com.ssu.assu.data.repository.usage

import com.ssu.assu.data.dto.certification.response.NoneDataResponseDto
import com.ssu.assu.data.dto.usage.SaveUsageRequestDto
import com.ssu.assu.domain.model.usage.MonthUsageModel
import com.ssu.assu.domain.model.usage.UnreviewedModel
import com.ssu.assu.util.RetrofitResult

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