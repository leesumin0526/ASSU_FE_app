package com.assu.app.domain.usecase.review
import com.assu.app.data.dto.review.request.ReviewWriteRequestDto
import com.assu.app.data.dto.review.response.ReviewWriteResponseDto
import com.assu.app.data.repository.review.ReviewRepository
import com.assu.app.util.RetrofitResult
import okhttp3.MultipartBody
import javax.inject.Inject

class WriteReviewUseCase @Inject constructor(
    private val repo : ReviewRepository
) {
    suspend operator fun invoke(
        request: ReviewWriteRequestDto,
        images: List<MultipartBody.Part>
    ): RetrofitResult<ReviewWriteResponseDto> {
        return repo.writeReview(request, images)
    }
}