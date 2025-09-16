package com.example.assu_fe_app.domain.usecase.review
import com.example.assu_fe_app.data.dto.review.request.ReviewWriteRequestDto
import com.example.assu_fe_app.data.dto.review.response.ReviewWriteResponseDto
import com.example.assu_fe_app.data.repository.review.ReviewRepository
import com.example.assu_fe_app.util.RetrofitResult
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