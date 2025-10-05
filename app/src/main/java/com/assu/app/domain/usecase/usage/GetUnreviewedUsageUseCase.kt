package com.assu.app.domain.usecase.usage

import com.assu.app.data.repository.usage.UsageRepository
import com.assu.app.domain.model.usage.UnreviewedModel
import com.assu.app.util.RetrofitResult
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