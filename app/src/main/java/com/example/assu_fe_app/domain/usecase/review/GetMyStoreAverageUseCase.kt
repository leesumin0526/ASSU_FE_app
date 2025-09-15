package com.example.assu_fe_app.domain.usecase.review

import com.example.assu_fe_app.data.dto.review.response.ReviewAverageResponseDto
import com.example.assu_fe_app.data.repository.review.ReviewRepository
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class GetMyStoreAverageUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
){
    suspend operator fun invoke(): RetrofitResult<ReviewAverageResponseDto> {
        return reviewRepository.getMyStoreAverage()
    }
}