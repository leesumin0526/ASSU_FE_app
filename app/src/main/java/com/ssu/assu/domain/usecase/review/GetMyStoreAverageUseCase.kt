package com.ssu.assu.domain.usecase.review

import com.ssu.assu.data.dto.review.response.ReviewAverageResponseDto
import com.ssu.assu.data.repository.review.ReviewRepository
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class GetMyStoreAverageUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
){
    suspend operator fun invoke(): RetrofitResult<ReviewAverageResponseDto> {
        return reviewRepository.getMyStoreAverage()
    }
}