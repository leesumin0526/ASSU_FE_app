package com.example.assu_fe_app.domain.usecase.usage

import com.example.assu_fe_app.data.dto.usage.SaveUsageRequestDto
import com.example.assu_fe_app.data.dto.usage.response.SaveUsageResponseDto
import com.example.assu_fe_app.data.repository.usage.UsageRepository
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class SaveUsageUseCase @Inject constructor(
    private val repo: UsageRepository
) {
    suspend operator fun invoke(
        request: SaveUsageRequestDto
    ) :
            RetrofitResult<SaveUsageResponseDto> {
        return repo.postUsage(request)
    }
}