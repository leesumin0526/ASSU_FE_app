package com.assu.app.domain.usecase.review

import com.assu.app.data.dto.review.response.ReviewAverageResponseDto
import com.assu.app.data.repository.review.ReviewRepository
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class GetMyStoreAverageUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
){
    suspend operator fun invoke(): RetrofitResult<ReviewAverageResponseDto> {
        return reviewRepository.getMyStoreAverage()
    }
}