package com.ssu.assu.domain.usecase.usage

import com.ssu.assu.data.repository.usage.UsageRepository
import com.ssu.assu.domain.model.usage.MonthUsageModel
import com.ssu.assu.util.RetrofitResult
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