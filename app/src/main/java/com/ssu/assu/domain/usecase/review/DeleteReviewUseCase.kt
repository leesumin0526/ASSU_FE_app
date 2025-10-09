package com.ssu.assu.domain.usecase.review

import com.ssu.assu.data.dto.review.response.DeleteReviewResponseDto
import com.ssu.assu.data.repository.review.ReviewRepository
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class DeleteReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository

) {
    suspend operator fun invoke(reviewId: Long): RetrofitResult<DeleteReviewResponseDto> {
        return reviewRepository.deleteReview(reviewId)
    }

}