package com.ssu.assu.domain.usecase.review
import com.ssu.assu.data.dto.review.request.ReviewWriteRequestDto
import com.ssu.assu.data.dto.review.response.ReviewWriteResponseDto
import com.ssu.assu.data.repository.review.ReviewRepository
import com.ssu.assu.util.RetrofitResult
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