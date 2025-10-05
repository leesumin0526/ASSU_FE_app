package com.assu.app.domain.usecase.usage

import com.assu.app.data.dto.certification.response.NoneDataResponseDto
import com.assu.app.data.dto.usage.SaveUsageRequestDto
import com.assu.app.data.repository.usage.UsageRepository
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class SaveUsageUseCase @Inject constructor(
    private val repo: UsageRepository
) {
    suspend operator fun invoke(
        request: SaveUsageRequestDto
    ) :
            RetrofitResult<NoneDataResponseDto> {
        return repo.postUsage(request)
    }
}