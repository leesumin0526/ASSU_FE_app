package com.ssu.assu.domain.usecase.review

import com.ssu.assu.data.dto.review.response.ReviewAverageResponseDto
import com.ssu.assu.data.repository.review.ReviewRepository
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class GetUserStoreReviewAverageUseCase @Inject constructor(
    private val repo: ReviewRepository
) {
    suspend operator fun invoke(storeId: Long
    ) : RetrofitResult<ReviewAverageResponseDto>
    { return repo.getUserStoreAverage(storeId) }

}