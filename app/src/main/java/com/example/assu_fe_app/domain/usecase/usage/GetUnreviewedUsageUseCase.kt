package com.example.assu_fe_app.domain.usecase.usage

import com.example.assu_fe_app.data.dto.usage.response.GetUnreviewedUsageDto
import com.example.assu_fe_app.data.repository.usage.UsageRepository
import com.example.assu_fe_app.domain.model.usage.UnreviewedModel
import com.example.assu_fe_app.util.RetrofitResult
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