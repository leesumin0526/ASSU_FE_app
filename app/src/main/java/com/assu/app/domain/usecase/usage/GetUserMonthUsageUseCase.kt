package com.assu.app.domain.usecase.usage

import com.assu.app.data.repository.usage.UsageRepository
import com.assu.app.domain.model.usage.MonthUsageModel
import com.assu.app.util.RetrofitResult
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