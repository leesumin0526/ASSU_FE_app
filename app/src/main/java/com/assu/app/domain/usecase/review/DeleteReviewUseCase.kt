package com.assu.app.domain.usecase.review

import com.assu.app.data.dto.review.response.DeleteReviewResponseDto
import com.assu.app.data.repository.review.ReviewRepository
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class DeleteReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository

) {
    suspend operator fun invoke(reviewId: Long): RetrofitResult<DeleteReviewResponseDto> {
        return reviewRepository.deleteReview(reviewId)
    }

}