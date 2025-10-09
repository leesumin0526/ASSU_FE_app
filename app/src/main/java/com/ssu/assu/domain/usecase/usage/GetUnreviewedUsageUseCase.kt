package com.ssu.assu.domain.usecase.usage

import com.ssu.assu.data.repository.usage.UsageRepository
import com.ssu.assu.domain.model.usage.UnreviewedModel
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class GetUnreviewedUsageUseCase @Inject constructor(
    private val repo : UsageRepository
) {

    suspend operator fun invoke(
        page: Int,
        size: Int,
        sort: String
    ) : RetrofitResult<UnreviewedModel>
    {
        return repo.getUnreviewedUsage(page, size, sort)
    }

}