package com.example.assu_fe_app.domain.usecase.review

import com.example.assu_fe_app.data.dto.review.response.ReviewAverageResponseDto
import com.example.assu_fe_app.data.repository.review.ReviewRepository
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class GetUserStoreReviewAverageUseCase @Inject constructor(
    private val repo: ReviewRepository
) {
    suspend operator fun invoke(storeId: Long
    ) : RetrofitResult<ReviewAverageResponseDto>
    { return repo.getUserStoreAverage(storeId) }

}