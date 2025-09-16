package com.example.assu_fe_app.domain.usecase.review

import com.example.assu_fe_app.data.dto.review.response.DeleteReviewResponseDto
import com.example.assu_fe_app.data.repository.review.ReviewRepository
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class DeleteReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository

) {
    suspend operator fun invoke(reviewId: Long): RetrofitResult<DeleteReviewResponseDto> {
        return reviewRepository.deleteReview(reviewId)
    }

}