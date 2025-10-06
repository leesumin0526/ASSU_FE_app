package com.ssu.assu.domain.usecase.usage

import com.ssu.assu.data.dto.certification.response.NoneDataResponseDto
import com.ssu.assu.data.dto.usage.SaveUsageRequestDto
import com.ssu.assu.data.repository.usage.UsageRepository
import com.ssu.assu.util.RetrofitResult
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