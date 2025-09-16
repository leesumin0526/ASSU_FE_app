package com.example.assu_fe_app.domain.usecase.usage

import com.example.assu_fe_app.data.dto.usage.response.UserMonthUsageResponseDto
import com.example.assu_fe_app.data.repository.usage.UsageRepository
import com.example.assu_fe_app.domain.model.usage.MonthUsageModel
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class GetUserMonthUsageUseCase @Inject constructor(
    private val repo: UsageRepository
) {
    suspend operator fun invoke(
        year: Int,
        month: Int
    ) : RetrofitResult<MonthUsageModel>{
        return repo.getMonthUsage(year, month)
    }

}