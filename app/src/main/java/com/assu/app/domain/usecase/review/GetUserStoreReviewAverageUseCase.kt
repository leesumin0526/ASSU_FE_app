package com.assu.app.domain.usecase.review

import com.assu.app.data.dto.review.response.ReviewAverageResponseDto
import com.assu.app.data.repository.review.ReviewRepository
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class GetUserStoreReviewAverageUseCase @Inject constructor(
    private val repo: ReviewRepository
) {
    suspend operator fun invoke(storeId: Long
    ) : RetrofitResult<ReviewAverageResponseDto>
    { return repo.getUserStoreAverage(storeId) }

}